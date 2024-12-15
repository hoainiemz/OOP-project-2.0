package graph;

import definitions.Constants;

public class FollowEdge extends ActionEdge {
    public FollowEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Constants.FOLLOW_EDGE_WEIGHT; }
    public String getType() { return "FollowEdge"; }
}