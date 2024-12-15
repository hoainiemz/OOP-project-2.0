package graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import std.Pair;
import definitions.Constants;

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
