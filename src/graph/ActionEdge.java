package graph;

import std.Pair;
import definitions.Constants;

public abstract class ActionEdge extends Pair<Node, Node> {
    public ActionEdge(Node x, Node y) {
        super(x, y);
    }
    public abstract double getWeight();
    public abstract String getType();
}
class FollowEdge extends ActionEdge {
    public FollowEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Constants.FOLLOW_EDGE_WEIGHT; }
    public String getType() { return "FollowEdge"; }
}
class CommentEdge extends ActionEdge {
    public CommentEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Constants.COMMENT_EDGE_WEIGHT; }
    public String getType() { return "CommentEdge"; }
}
class TweetEdge extends ActionEdge {
    public TweetEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Constants.TWEET_EDGE_WEIGHT; }
    public String getType() { return "TweetEdge"; }
}
class RepostEdge extends ActionEdge {
    public RepostEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Constants.REPOST_EDGE_WEIGHT; }
    public String getType() { return "RepostEdge"; }
}
