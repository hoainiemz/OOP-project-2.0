package twitterCrawler;

public class CrawlOptions {
    private int kolMinFollower;
    private int maxTweetsPerKol;
    private int maxRepliesPerTweet;
    private int maxUserPerKeyword;
    private String url;
    private static final String FOLLOWER_SELECTOR =  "body > div > div > div.profile-tab.sticky > div > div.profile-card-extra > div.profile-card-extra-links > ul > li.followers > span.profile-stat-num";
    private static final String CRAWL_SHOW_MORE_SELECTOR = "body > div.container > div > div.timeline-container > div > .show-more:not(.timeline-item)";
    private static final String TIMELINE_ITEM_SELECTOR = "body > div.container > div > div.timeline-container > div > div.timeline-item:not(.show-more)";
    private static final String SEARCH_SHOW_MORE_SELECTOR = "body > div.container > div > div.timeline > div.show-more:not(.timeline-item)";
    private static final String SEARCH_TIMELINE_ITEM_SELECTOR = "body > div > div > div.timeline > div:not(.show-more)";
    private String href = null;
    public String getSearchShowMoreSelector() {return SEARCH_SHOW_MORE_SELECTOR;}

    public String getCrawlShowMoreSelector() {
        return CRAWL_SHOW_MORE_SELECTOR;
    }

    private void init() {
        kolMinFollower = 100000;
        maxTweetsPerKol = 2;
        maxRepliesPerTweet = 2;
        maxUserPerKeyword = 2;
    }

    public CrawlOptions() {
        init();
        setUrl("https://nitter.poast.org/");
    }

    public CrawlOptions(String url) {
        init();
        setUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSearchTimelineItemSelector() {
        return SEARCH_TIMELINE_ITEM_SELECTOR;
    }

    public String getTimelineItemSelector() {
        return TIMELINE_ITEM_SELECTOR;
    }

    public String getFollowerSelector() {
        return FOLLOWER_SELECTOR;
    }

    public int getMaxUserPerKeyword() {
        return maxUserPerKeyword;
    }

    public void setMaxUserPerKeyword(int maxUserPerKeyword) {
        this.maxUserPerKeyword = maxUserPerKeyword;
    }

    public int getMaxTweetsPerKol() {
        return maxTweetsPerKol;
    }

    public void setMaxTweetsPerKol(int maxTweetsPerKol) {
        this.maxTweetsPerKol = maxTweetsPerKol;
    }

    public int getMaxRepliesPerTweet() {
        return maxRepliesPerTweet;
    }

    public void setMaxRepliesPerTweet(int maxRepliesPerTweet) {
        this.maxRepliesPerTweet = maxRepliesPerTweet;
    }

    public int getKolMinFollower() {
        return kolMinFollower;
    }

    public void setKolMinFollower(int kolMinFollower) {
        this.kolMinFollower = kolMinFollower;
    }

    public int getMaxTweetPerUser() {
        return maxTweetsPerKol;
    }

    public void setMaxTweetPerUser(int maxTweetsPerKol) {
        this.maxTweetsPerKol = maxTweetsPerKol;
    }
}
