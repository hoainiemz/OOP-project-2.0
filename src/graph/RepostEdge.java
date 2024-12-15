package graph;

import definitions.Constants;

public class RepostEdge extends ActionEdge {
    public RepostEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Constants.REPOST_EDGE_WEIGHT; }
    public String getType() { return "RepostEdge"; }
}
