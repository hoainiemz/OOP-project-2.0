package twitter;

import grapheditor.*;
import json.JSON;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import std.Str;
import std.StringComparator;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Twitter{
    private CrawlOptions options;
    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;

    public Twitter(CrawlOptions options) throws InterruptedException {
        this.options = options;
        ChromeOptions driverOptions = new ChromeOptions();
        driverOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        this.driver = new ChromeDriver(driverOptions);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        visit(options.getUrl());
        TimeUnit.SECONDS.sleep(5);
    }

    /**
     * navigate the browser to url
     * @param url
     * @throws InterruptedException
     */
    public void visit(String url) throws InterruptedException {
        int i = 0;
        if (!url.startsWith("https://")) {
            url = "https://" + url;
        }
        while (true) {
            try {
                this.driver.navigate().to(url);
                break;
            }
            catch(Exception InterruptedException) {
                continue;
            }
        }
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * @param selector
     * @return the WebElement given the CSS slector
     */
    private WebElement findElement(String selector) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
    }

    /**
     * crawl the tweet, add the crawled edges to graph
     * @param tweet
     * @param graph
     * @throws InterruptedException
     * @throws IOException
     */
    public void crawlTweet(Node tweet, GraphEditor graph) throws InterruptedException, IOException {
        visit(tweet.getUrl(options));
        while (true) {
            int cnt = 0;
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
                graph.addEdge(commenter, tweet);
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

    /**
     * crawl the user, save the crawled data to the json file corresponding to the user
     * @param user
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean crawlUser(Node user) throws InterruptedException, IOException {
        if (GraphEditor.crawled(user)) {
            return false;
        }
        visit(user.getUrl(options));
        GraphEditor graph = new GraphEditor();
        int followers = Str.stoi(findElement(options.getFollowerSelector()).getAttribute("innerHTML"));
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
                graph.addEdge(tweet, user);
                crawlTweet(tweet, graph);
            }
            else {
                graph.addEdge(user, tweet);
            }
        }
        graph.save(user);
        return true;
    }

    /**
     * crawl usernames from given keyword and push into list
     * @param keyword
     * @param list
     * @throws InterruptedException
     * @throws IOException
     */
    public void crawlKeyword(String keyword, ArrayList<String> list) throws InterruptedException, IOException {
        String url = "https://nitter.poast.org/search?f=users&q=" + keyword;
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
            JSON.dumpToJSON(list, "usernames.json");
            showMore.click();
        }
    }

    /**
     * crawl the keyword
     * @throws IOException
     * @throws InterruptedException
     */
    public void search() throws IOException, InterruptedException {
        ArrayList<String> keyWords = JSON.loadArrayFromJSON("searchingkeywords.json");
        ArrayList<String> handleList = new ArrayList<>();
        for (String keyWord : keyWords) {
           crawlKeyword(keyWord, handleList);
        }
        JSON.dumpToJSON(handleList, "usernames.json");
    }

    /**
     * crawl the user
     * @throws IOException
     * @throws InterruptedException
     */
    public void crawl() throws IOException, InterruptedException {
        ArrayList<String> handles = JSON.loadArrayFromJSON("usernames.json");
        TreeSet<String> skipped = new TreeSet<>(new StringComparator());
        skipped.addAll(JSON.loadArrayFromJSON("skipped.json"));
        for (String handle : handles) {
            if (skipped.contains(handle)) {
                continue;
            }
            crawlUser(new Node(handle));
            skipped.add(handle);
            JSON.dumpToJSON(new ArrayList<>(skipped), "skipped.json");
        }
        JSON.dumpToJSON(new ArrayList<>(skipped), "skipped.json");
        System.out.println("Done! :)");
    }

    /**
     * load twitter cookies into the browser
     * @param file
     * @throws InterruptedException
     * @throws IOException
     */
    public void loadCookies(String file) throws InterruptedException, IOException {
        ArrayList<LinkedHashMap<String, Object> > cookies = (ArrayList<LinkedHashMap<String, Object>>) ((LinkedHashMap<String, Object>) JSON.loadObjectFromJSON(file)).get("cookies");
        for (LinkedHashMap<String, Object> cookie : cookies) {
//            System.out.println(cookie);
            String name = cookie.get("name").toString();
            String value = cookie.get("value").toString();
            Cookie c = new Cookie(name, value);
            driver.manage().addCookie(c);
        }
        driver.navigate().refresh();
    }

    private void autoCrawlFollowers(TreeSet<String> response) {
        try {
            findElement("#react-root > div > div > div.css-175oi2r.r-1f2l425.r-13qz1uu.r-417010.r-18u37iz > main > div > div > div > div > div > section > div > div > .css-175oi2r");
        } catch (Exception e) {
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

    /**
     * for all crawled users, add all of their following edges to data
     */
    public void updateFollowingEdges() throws IOException, InterruptedException {
        ArrayList<String> handles = JSON.loadArrayFromJSON("usernames.json");
        loadCookies("cookies.json");
        TreeSet<String> skipped = new TreeSet<>(new StringComparator());
        skipped.addAll(JSON.loadArrayFromJSON("skipped.json"));
        for (String handle : handles) {
            if (!JSON.exists("crawled/" + handle + ".json") || skipped.contains(handle)) {
                continue;
            }
            GraphEditor graph = new GraphEditor();
            graph.loadFromFile(handle + ".json");
            ArrayList<String> followingList = crawlFollowingList(new Node(handle));
            for (String tmp : followingList) {
                graph.addEdge(handle, tmp);
            }
            ArrayList<String> followersList = crawlFollowersList(new Node(handle));
            for (String tmp : followersList) {
                graph.addEdge(tmp, handle);
            }
            skipped.add(handle);
            graph.save(new Node(handle));
            JSON.dumpToJSON(new ArrayList<>(skipped), "skipped.json");
        }
    }
}
