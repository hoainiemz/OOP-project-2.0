package program;

import grapheditor.Edge;
import grapheditor.Grapher;
import grapheditor.Node;

import pagerank.DirectedEdge;
import pagerank.WeightedPagerank;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TableRow implements Comparable<TableRow> {
    String handle;
    double point;
    public TableRow(String handle, double point) {
        this.handle = handle;
        this.point = point;
    }
    @Override
    public int compareTo(TableRow o) {
        return point < o.point ? 1 : (point > o.point ? -1 : 0);
    }
}

public class Main {
    static void insertNode(TreeMap<Node, Integer> tr, Node node) {
        if(tr.containsKey(node)) {
            return;
        }
        tr.put(node, tr.size() + 1);
    }

    static TreeSet<Node> loadKol() throws IOException {
        Stream<Path> stream;
        try {
            stream = Files.list(Paths.get("data/crawled"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Set<String> files = stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName).map(Path::toString).collect(Collectors.toSet());
        TreeSet<Node> kol = new TreeSet<>();
        for(String file : files) {
            if(file.endsWith(".json")) {
                kol.add(new Node(file.substring(0, file.length() - 5)));
            }
        }
        return kol;
    }

    static void printTable(String fp, ArrayList<TableRow> table) throws FileNotFoundException {
        PrintStream outstream = null;
        try {
            outstream = new PrintStream(new FileOutputStream(fp));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        PrintWriter out = new PrintWriter(outstream);
        out.println("rank,handle,point");
        Integer count = 0;
        for(TableRow t : table) {
            out.printf("%d,%s,%f\n", ++count, t.handle, t.point);
        }
        out.flush();
    }

    public static void main(String[] args) throws IOException {
        Grapher grapher = new Grapher();
        try {
            grapher.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Edge> edges = grapher.getEdgesList();
        TreeMap<Node, Integer> id = new TreeMap<>();
        for(Edge e : edges) {
            insertNode(id, e.getKey());
            insertNode(id, e.getValue());
        }
        int n = id.size(); // number of vertices
        ArrayList<DirectedEdge>[] adj = new ArrayList[n + 1];
        for(int i = 1; i <= n; ++i) {
            adj[i] = new ArrayList<>();
        }
        for(Edge e : edges) {
            int u = id.get(e.getKey());
            int v = id.get(e.getValue());
            adj[u].add(new DirectedEdge(v, e.getWeight()));
        }

        double[] biases = new double[n + 1];
        TreeSet<Node> kolList = loadKol();
        for(Map.Entry<Node, Integer> it : id.entrySet()) {
            Node nd = it.getKey();
            int u = it.getValue();
            if(nd.getXpath().isEmpty()) {
                biases[u] = kolList.contains(nd) ? Defs.kolBias : Defs.normieBias;
            }
            else {
                biases[u] = Defs.tweetBias;
            }
        }

        WeightedPagerank pr = new WeightedPagerank();
        double[] points = pr.pagerank(Defs.epoches, n, adj, Defs.c, biases);
        ArrayList<TableRow> table = new ArrayList<>();
        for(Node nd : kolList) {
            assert(id.containsKey(nd));
            int index = id.get(nd);
            table.add(new TableRow(nd.getUser(), points[index]));
        }
        table.sort(TableRow::compareTo);
        printTable(Defs.outputFilePath, table);
    }
}
