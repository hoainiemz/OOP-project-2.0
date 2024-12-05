package grapheditor;

import std.Pair;
import program.Defs;

public abstract class Edge extends Pair<Node, Node> {
    public Edge(Node x, Node y) {
        super(x, y);
    }
    public abstract double getWeight();
    public abstract String edgeType();
}
class FollowEdge extends Edge {
    public FollowEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Defs.followEdgeWeight; }
    public String edgeType() { return "FollowEdge"; }
}
class CommentEdge extends Edge {
    public CommentEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Defs.commentEdgeWeight; }
    public String edgeType() { return "CommentEdge"; }
}
class TweetEdge extends Edge {
    public TweetEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Defs.tweetEdgeWeight; }
    public String edgeType() { return "TweetEdge"; }
}
class RepostEdge extends Edge {
    public RepostEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Defs.repostEdgeWeight; }
    public String edgeType() { return "RepostEdge"; }
}