package program;

import definitions.Constants;

import graph.ActionEdge;
import graph.ActionGraph;
import graph.Node;

import pagerank.DirectedEdge;
import pagerank.WeightedPageRankRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class KolRankListTableRow implements Comparable<KolRankListTableRow> {
    String handle;
    double point;
    public KolRankListTableRow(String handle, double point) {
        this.handle = handle;
        this.point = point;
    }
    @Override
    public int compareTo(KolRankListTableRow o) {
        return point < o.point ? 1 : (point > o.point ? -1 : 0);
    }
}

class kolLoader {
    static TreeSet<Node> loadKol() throws IOException {
        Stream<Path> stream;
        try {
            stream = Files.list(Paths.get(Constants.CRAWLED_DATA_PREFIX_PATH));
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
}

// day chinh la chuong trinh de tao do thi
// va tạo bang xep hang
// ket qua cuoi cung se duoc luu vao file result.csv
// ket qua duoc luu duoi dinh dang .csv
public class KolRankListExporter {
    static void printTable(String fp, ArrayList<KolRankListTableRow> table) throws FileNotFoundException {
        PrintStream outstream = null;
        try {
            outstream = new PrintStream(new FileOutputStream(fp));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        PrintWriter out = new PrintWriter(outstream);
        out.println("rank,handle,point");
        Integer count = 0;
        for(KolRankListTableRow t : table) {
            out.printf("%d,%s,%f\n", ++count, t.handle, t.point);
        }
        out.flush();
    }

    public static void main(String[] args) throws IOException {
        ActionGraph graph = new ActionGraph();
        graph.load();
        ArrayList<ActionEdge> edges = graph.getEdgesList();
        TreeMap<Node, Integer> id = new TreeMap<>();

        TreeSet<Node> kolList = kolLoader.loadKol();
        Set<Node> nodeList = graph.getNodeList();
        nodeList.addAll(kolList);

        Integer n = Integer.valueOf(1);
        for(Node nd : nodeList) {
            n = Integer.valueOf(n + 1);
            id.put(nd, n);
        }

        ArrayList<DirectedEdge>[] adj = new ArrayList[n + 1];
        for(int i = 1; i <= n; ++i) {
            adj[i] = new ArrayList<>();
        }
        for(ActionEdge e : edges) {
            int u = id.get(e.getKey());
            int v = id.get(e.getValue());
            adj[u].add(new DirectedEdge(v, e.getWeight()));
        }

        double[] biases = new double[n + 1];
        for(Map.Entry<Node, Integer> it : id.entrySet()) {
            Node nd = it.getKey();
            int u = it.getValue();
            if(nd.getXpath().isEmpty()) {
                biases[u] = kolList.contains(nd) ? Constants.KOL_BIAS : Constants.NORMIE_BIAS;
            }
            else {
                biases[u] = Constants.TWEET_BIAS;
            }
        }

        double[] points = WeightedPageRankRunner.pageRank(Constants.PAGERANK_EPOCHES, n, adj, Constants.PAGERANK_C, biases);
        ArrayList<KolRankListTableRow> table = new ArrayList<>();
        for(Node nd : kolList) {
            int index = id.get(nd);
            table.add(new KolRankListTableRow(nd.getUser(), points[index]));
        }
        table.sort(KolRankListTableRow::compareTo);
        printTable(Constants.KOL_RANK_LIST_OUTPUT_FILE_PATH, table);
    }
}
