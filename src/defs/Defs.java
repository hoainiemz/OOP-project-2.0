package defs;

public class Defs {
    final static public int pagerankEpoches = 5000;
    final static public double pagerankC = 0.8;

    final static public double followEdgeWeight = 0.6;
    final static public double commentEdgeWeight = 0.3;
    final static public double tweetEdgeWeight = 5;
    final static public double repostEdgeWeight = 1;

    final static public double kolBias = 5;
    final static public double normieBias = 0.1;
    final static public double tweetBias = 0.2;

    final static public String outputFilePath = "result.csv";
}
