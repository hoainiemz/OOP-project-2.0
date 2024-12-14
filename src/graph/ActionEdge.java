package graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import std.Pair;
import definitions.Constants;

import java.io.Serializable;

public abstract class ActionEdge extends Pair<Node, Node> implements Comparable<ActionEdge> {
    public ActionEdge(Node x, Node y) {
        super(x, y);
    }

    @Override
    public int compareTo(ActionEdge rhs) {
        int tmp = this.getKey().compareTo(rhs.getKey());
        return (tmp == 0) ? this.getValue().compareTo(rhs.getValue()) : tmp;
    }

    @JsonIgnore
    public abstract double getWeight();

    @JsonIgnore
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
