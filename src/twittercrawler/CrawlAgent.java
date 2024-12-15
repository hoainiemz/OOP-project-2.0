package twittercrawler;

import definitions.Constants;
import graph.*;
import jsonhandler.JsonHandler;
import std.StringFunction;
import std.StringComparator;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class CrawlAgent {
    final private CrawlOptions options;
    final private WebDriver driver;
    private Actions actions;
    final private WebDriverWait wait;

    public CrawlAgent(CrawlOptions options) throws InterruptedException {
        this.options = options;
        ChromeOptions driverOptions = new ChromeOptions();
        driverOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        System.setProperty("webdriver.chrome.driver", Constants.CHROMEDRIVER_PATH);
        this.driver = new ChromeDriver(driverOptions);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        visit(options.getUrl());
        TimeUnit.SECONDS.sleep(5);
    }

    public void visit(String url) throws InterruptedException {
        if (!url.startsWith("https://")) {
            url = "https://" + url;
        }
        while (true) {
            try {
                this.driver.navigate().to(url);
                break;
            }
            catch(Exception e) {
                continue;
            }
        }
        TimeUnit.SECONDS.sleep(1);
    }

    private WebElement findElement(String selector) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
    }

    public void crawlTweet(Node tweet, ActionGraph graph) throws InterruptedException {
        visit(tweet.getUrl(options));
        int cnt = 0;
        while(true) {
            List<WebElement> replies = driver.findElements(By.cssSelector("#r > .reply.thread.thread-line"));
            for (WebElement reply : replies) {
                String handle;
                try {
                    handle = reply.findElement(By.cssSelector("a.username")).getAttribute("innerHTML");
                }
                catch (Exception NoSuchElementException) {
                    continue;
                }
                if (handle.charAt(0) == '@') {
                    handle = handle.substring(1);
                }
                if (handle.compareTo(tweet.getUser()) == 0) {
                    continue;
                }
                Node commenter = new Node(handle);
                graph.addCommentEdge(commenter, tweet);
                cnt++;
                if (cnt >= options.getMaxRepliesPerTweet()) {
                    break;
                }
            }
            if (cnt >= options.getMaxRepliesPerTweet()) {
                break;
            }
            try {
                WebElement showMore = driver.findElement(By.cssSelector("#r > .show-more"));
                showMore.click();
            }
            catch(Exception NoSuchElementException) {
                break;
            }
        }
    }

    public boolean crawlUser(Node user) throws InterruptedException, IOException {
        if (ActionGraph.isCrawled(user)) {
            return false;
        }
        visit(user.getUrl(options));
        ActionGraph graph = new ActionGraph();
        int followers = StringFunction.sToI(findElement(options.getFollowerSelector()).getAttribute("innerHTML"));
        if (followers < options.getKolMinFollower()) {
            return false;
        }
        List<String> tweets = new ArrayList<>();
        int cnt = 0;
        while (true) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(options.getCrawlShowMoreSelector())));
            }
            catch (Exception TimeoutException) {
                return true;
            }
            WebElement showMore = driver.findElement(By.cssSelector(options.getCrawlShowMoreSelector()));
            List<WebElement> timelineItem = driver.findElements(By.cssSelector(options.getTimelineItemSelector()));
            if (cnt >= options.getMaxTweetPerUser()) {
                break;
            }
            for (int i = 0; cnt < options.getMaxTweetPerUser() && i < timelineItem.size(); i++) {
                WebElement tweet = timelineItem.get(i);
                String href = tweet.findElement(By.cssSelector("a.tweet-link")).getAttribute("href");
                tweets.add(href);
                cnt++;
            }
            showMore.click();
        }
        for (String url : tweets) {
            Node tweet = Node.constructFromTweetUrl(url);
            if (user.getUser().compareTo(tweet.getUser()) == 0) {
                graph.addTweetEdge(tweet, user);
                crawlTweet(tweet, graph);
            }
            else {
                graph.addRepostEdge(user, tweet);
            }
        }
        graph.save(user);
        return true;
    }

    public void crawlKeyword(String keyword, TreeSet<String> list) throws InterruptedException, IOException {
        String url = Constants.NITTER_URL + "search?f=users&q=%23" + keyword;
        visit(url);
        int cnt = 0;
        while (true) {
            if (cnt >= options.getMaxUserPerKeyword()) {
                break;
            }
            WebElement showMore;
            ArrayList<WebElement> timelineItem = (ArrayList<WebElement>) driver.findElements(By.cssSelector(options.getSearchTimelineItemSelector()));
            try {
                showMore = driver.findElement(By.cssSelector(options.getSearchShowMoreSelector()));
            }
            catch(Exception NoSuchElementException) {
                break;
            }
            for (WebElement user : timelineItem) {
                String handle = user.findElement(By.cssSelector("a.username")).getAttribute("innerHTML");
                if (handle.charAt(0) == '@') {
                    handle = handle.substring(1);
                }
                list.add(handle);
                cnt++;
                if (cnt >= options.getMaxUserPerKeyword()) {
                    break;
                }
            }
            JsonHandler.dumpToJSON(list, Constants.USERNAME_PATH);
            showMore.click();
        }
    }

    public void search() throws IOException, InterruptedException {
        ArrayList<String> keyWords = JsonHandler.loadArrayFromJSON(Constants.SEARCHING_KEYWORDS_PATH);
        TreeSet<String> handleList = new TreeSet<>(new std.StringComparator());
        for (String keyWord : keyWords) {
            crawlKeyword(keyWord, handleList);
        }
        JsonHandler.dumpToJSON(handleList, Constants.USERNAME_PATH);
    }

    public void crawl() throws IOException, InterruptedException {
        ArrayList<String> handles = JsonHandler.loadArrayFromJSON(Constants.USERNAME_PATH);
        TreeSet<String> skipped = new TreeSet<>(new StringComparator());
        skipped.addAll(JsonHandler.loadArrayFromJSON(Constants.SKIPPED_PATH));
        for (String handle : handles) {
            if (skipped.contains(handle)) {
                continue;
            }
            crawlUser(new Node(handle));
            skipped.add(handle);
            JsonHandler.dumpToJSON(new ArrayList<>(skipped), Constants.SKIPPED_PATH);
        }
        JsonHandler.dumpToJSON(new ArrayList<>(skipped), Constants.SKIPPED_PATH);
        System.out.println("Done! :)");
    }

    public void loadCookies(String file) throws InterruptedException, IOException {
        ArrayList<LinkedHashMap<String, Object> > cookies = (ArrayList<LinkedHashMap<String, Object>>) ((LinkedHashMap<String, Object>) JsonHandler.loadObjectFromJSON(file)).get("cookies");
        for (LinkedHashMap<String, Object> cookie : cookies) {
//            System.out.println(cookie);
            String name = cookie.get("name").toString();
            String value = cookie.get("value").toString();
            Cookie c = new Cookie(name, value);
            driver.manage().addCookie(c);
        }
        driver.navigate().refresh();
    }

    private boolean handleRetry(String selector) throws InterruptedException {
        while (true) {
            try {
                findElement(selector);
                break;
            }
            catch (Exception e) {
                try {
                    WebElement retry = findElement(options.getFollowerRetrySelector());
                    TimeUnit.MINUTES.sleep(2);
                    retry.click();
                    continue;
                } catch (Exception e2) {
                    try {
                        WebElement home = findElement(options.getXHomeButton());
                        return false;
                    } catch (Exception e3) {
                        TimeUnit.MINUTES.sleep(2);
                        driver.navigate().refresh();
                    }
                }
            }
        }
        return true;
    }
    
    private void autoCrawlFollowers(TreeSet<String> response) throws InterruptedException {
        if (!handleRetry("#react-root > div > div > div.css-175oi2r.r-1f2l425.r-13qz1uu.r-417010.r-18u37iz > main > div > div > div > div > div > section > div > div > .css-175oi2r")) {
            return;
        }
        while (true) {
            try {
                WebElement last = null;
                List<WebElement> list = driver.findElements(By.cssSelector("#react-root > div > div > div.css-175oi2r.r-1f2l425.r-13qz1uu.r-417010.r-18u37iz > main > div > div > div > div > div > section > div > div > .css-175oi2r"));
                for (WebElement follower : list) {
                    String handle = follower.findElement(By.cssSelector("div > div > button > div > div.css-175oi2r.r-1iusvr4.r-16y2uox > div.css-175oi2r.r-1awozwy.r-18u37iz.r-1wtj0ep > div.css-175oi2r.r-1wbh5a2.r-dnmrzs.r-1ny4l3l > div > div.css-175oi2r.r-1awozwy.r-18u37iz.r-1wbh5a2 > div > a > div > div > span")).getAttribute("innerHTML");
                    response.add(handle);
                    last = follower;
                }
                if (last != null) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].scrollIntoView(true);", last);
                    js.executeScript("window.scrollBy(0, -100);");
                    Thread.sleep(1000);
                }
            }
            catch(Exception e) {
                break;
            }
        }
    }

    public ArrayList<String> crawlFollowersList(Node user) throws InterruptedException {
        TreeSet<String> response = new TreeSet<>(new StringComparator());
        visit(user.getUrl(options) + "/verified_followers");
        autoCrawlFollowers(response);
        visit(user.getUrl(options) + "/followers");
        autoCrawlFollowers(response);
        return new ArrayList<>(response);
    }

    public ArrayList<String> crawlFollowingList(Node user) throws InterruptedException {
        TreeSet<String> response = new TreeSet<>(new StringComparator());
        visit(user.getUrl(options) + "/following");
        autoCrawlFollowers(response);
        return new ArrayList<>(response);
    }

    public void updateFollowingEdges() throws IOException, InterruptedException {
        ArrayList<String> handles = JsonHandler.loadArrayFromJSON(Constants.USERNAME_PATH);
        loadCookies(Constants.COOKIES_PATH);
        TreeSet<String> skipped = new TreeSet<>(new StringComparator());
        skipped.addAll(JsonHandler.loadArrayFromJSON(Constants.SKIPPED_PATH));
        for (String handle : handles) {
            String filepath = std.StringFunction.getJSONFilePath(handle);
            if (!JsonHandler.exists(filepath) || skipped.contains(handle)) {
                continue;
            }
            ActionGraph graph = new ActionGraph();
            graph.loadFromFile(filepath);
            ArrayList<String> followingList = crawlFollowingList(new Node(handle));
            for (String tmp : followingList) {
                graph.addFollowEdge(new Node(handle), new Node(tmp));
            }
            ArrayList<String> followersList = crawlFollowersList(new Node(handle));
            for (String tmp : followersList) {
                graph.addFollowEdge(new Node(tmp), new Node(handle));
            }
            skipped.add(handle);
            graph.save(new Node(handle));
            JsonHandler.dumpToJSON(new ArrayList<>(skipped), Constants.SKIPPED_PATH);
        }
    }
}
