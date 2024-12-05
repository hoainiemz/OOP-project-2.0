package graph;

import std.Pair;
import defs.Defs;

public abstract class ActionEdge extends Pair<Node, Node> {
    public ActionEdge(Node x, Node y) {
        super(x, y);
    }
    public abstract double getWeight();
    public abstract String edgeType();
}
class FollowEdge extends ActionEdge {
    public FollowEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Defs.followEdgeWeight; }
    public String edgeType() { return "FollowEdge"; }
}
class CommentEdge extends ActionEdge {
    public CommentEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Defs.commentEdgeWeight; }
    public String edgeType() { return "CommentEdge"; }
}
class TweetEdge extends ActionEdge {
    public TweetEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Defs.tweetEdgeWeight; }
    public String edgeType() { return "TweetEdge"; }
}
class RepostEdge extends ActionEdge {
    public RepostEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Defs.repostEdgeWeight; }
    public String edgeType() { return "RepostEdge"; }
}
