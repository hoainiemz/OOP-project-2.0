package graph;

import definitions.Constants;

public class TweetEdge extends ActionEdge {
    public TweetEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Constants.TWEET_EDGE_WEIGHT; }
    public String getType() { return "TweetEdge"; }
}
