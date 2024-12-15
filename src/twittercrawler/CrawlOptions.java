package twittercrawler;

import definitions.Constants;

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
    private static final String FOLLOWER_RETRY_SELECTOR = "#react-root > div > div > div.css-175oi2r.r-1f2l425.r-13qz1uu.r-417010.r-18u37iz > main > div > div > div > div > div > div.css-175oi2r.r-1awozwy.r-16y2uox.r-1777fci.r-dd0y9b.r-3o4zer.r-f8sm7e.r-13qz1uu.r-1ye8kvj > button";
    private static final String X_HOME_BUTTON = "#react-root > div > div > div.css-175oi2r.r-1f2l425.r-13qz1uu.r-417010.r-18u37iz > header > div > div > div > div:nth-child(1) > div.css-175oi2r.r-dnmrzs.r-1559e4e > h1";
    private String href = null;

    public String getXHomeButton() {return X_HOME_BUTTON;}

    public String getSearchShowMoreSelector() {return SEARCH_SHOW_MORE_SELECTOR;}

    public String getCrawlShowMoreSelector() {
        return CRAWL_SHOW_MORE_SELECTOR;
    }

    public String getFollowerRetrySelector() {return FOLLOWER_RETRY_SELECTOR;}

    private void init() {
        kolMinFollower = 100000;
        maxTweetsPerKol = 2; // tham so cho viec dao data
        maxRepliesPerTweet = 2; // tam thoi em se de it de quay video
        maxUserPerKeyword = 2;
    }

    public CrawlOptions() {
        init();
        setUrl(Constants.NITTER_URL);
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
