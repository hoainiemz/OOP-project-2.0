package pagerank;

public class DirectedEdge
{
    int v;
    double w; // an edge from u to v with weight w
    public DirectedEdge(int v, double w) {
        this.v = v;
        this.w = w;
    }
}
