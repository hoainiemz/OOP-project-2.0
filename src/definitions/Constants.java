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
}
