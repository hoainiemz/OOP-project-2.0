package graph;

import definitions.Constants;

public class CommentEdge extends ActionEdge {
    public CommentEdge(Node x, Node y) {
        super(x, y);
    }
    public double getWeight() { return Constants.COMMENT_EDGE_WEIGHT; }
    public String getType() { return "CommentEdge"; }
}