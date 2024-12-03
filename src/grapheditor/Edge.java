package grapheditor;

import std.Pair;

public abstract class Edge extends Pair<Node, Node> {
//    private static double weight = 0;
//    private static String EdgeType = "UntypedEdge";
    public Edge(Node x, Node y) {
        super(x, y);
    }

    public abstract String getType();

    public abstract double getWeight();
}

class FollowEdge extends Edge {
    private static double weight = 0.2;
    private static String EdgeType = "FollowEdge";

    public FollowEdge(Node x, Node y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return EdgeType;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}

class CommentEdge extends Edge {
    private static double weight = 0.3;
    private static String EdgeType = "CommentEdge";

    public CommentEdge(Node x, Node y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return EdgeType;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}

class TweetEdge extends Edge {
    private static double weight = 0.1;
    private static String EdgeType = "TweetEdge";

    public TweetEdge(Node x, Node y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return EdgeType;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}

class RepostEdge extends Edge {
    private static double weight = 0.1;
    private static String EdgeType = "RepostEdge";

    public RepostEdge(Node x, Node y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return EdgeType;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}