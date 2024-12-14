package definitions;

public class Constants {
    final static public int PAGERANK_EPOCHES = 5000;
    final static public double PAGERANK_C = 0.8;

    final static public double FOLLOW_EDGE_WEIGHT = 0.6;
    final static public double COMMENT_EDGE_WEIGHT = 0.3;
    final static public double TWEET_EDGE_WEIGHT = 5;
    final static public double REPOST_EDGE_WEIGHT = 1;

    final static public double KOL_BIAS = 5;
    final static public double NORMIE_BIAS = 0.1;
    final static public double TWEET_BIAS = 0.2;

    final static public String KOL_RANK_LIST_OUTPUT_FILE_PATH = "result.csv";

    final static public String CHROMEDRIVER_PATH = "/usr/lib/chromium-browser/chromedriver";

    final static public String USERNAME_PATH = "data/usernames.json";
    final static public String SEARCHING_KEYWORDS_PATH = "data/searchingkeywords.json";
    final static public String SKIPPED_PATH = "data/skipped.json";

    final static public String CRAWLED_DATA_PREFIX_PATH = "data/crawled/";
}
