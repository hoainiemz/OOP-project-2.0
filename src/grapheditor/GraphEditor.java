package grapheditor;


import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import org.jgrapht.*;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import std.Pair;
import std.Str;
import json.JSON;
import std.StringComparator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node p1, Node p2) {
        int tmp = p1.getUser().compareTo(p2.getUser());
        if (tmp == 0) {
            return p1.getXpath().compareTo(p2.getXpath());
        }
        return tmp;
    }
}

public class GraphEditor {
    private ArrayList<Pair<Node, Node>> edgesList;
    public GraphEditor() {
        edgesList = new ArrayList<>();
    }

    /**
     * add edge u -> v to the graph
     * @param u
     * @param v
     */
    public void addEdge(Node u, Node v) {
        edgesList.add(new Pair<Node, Node>(u, v));
    }

    /**
     * add edge u->v to the graph
     * @param u
     * @param v
     */
    public void addEdge(String u, String v) {
        edgesList.add(new Pair<Node, Node>(new Node(u), new Node(v)));
    }

    /**
     * visualize the graph
     * @param graph
     * @throws IOException
     */
    private static void visualizeGraph(Graph<String, DefaultEdge> graph) throws IOException {
        JGraphXAdapter<String, DefaultEdge> graphAdapter =
                new JGraphXAdapter<String, DefaultEdge>(graph);
//        graphAdapter.setLabelsVisible(false);
        graphAdapter.getEdgeToCellMap().forEach((edge, cell) -> cell.setValue(null));
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("graph.png");
        ImageIO.write(image, "PNG", imgFile);
    }

    /**
     * @param user
     * @return the file contains data of user
     */
    static public String getJSONFilename(Node user) {
        return "crawled/" + user.getUser() + ".json";
    }

    /**
     * @param user
     * @return the file contains data of user
     */
    static public String getJSONFilename(String user) {
        return "crawled/" + user + ".json";
    }

    /**
     * save the graph to the user's JSON file
     * @throws IOException
     */
    public void save(Node user) throws IOException {
        JSON.dumpToJSON(edgesList, getJSONFilename(user));
    }

    /**
     *
     * @param user
     * @return true if this user has been crawled
     */
    public static boolean crawled(Node user) {
        String nodeJSON = getJSONFilename(user);
        if (JSON.exists(nodeJSON)) {
            return true;
        }
        return false;
    }

    /**
     * load graph from file
     * @param file
     * @throws IOException
     */
    public void loadFromFile(String file) throws IOException {
        ArrayList<LinkedHashMap<LinkedHashMap<String, String>, LinkedHashMap<String, String> >> edges = JSON.loadArrayFromJSON("/crawled/" + file);
        for (LinkedHashMap<LinkedHashMap<String, String>, LinkedHashMap<String, String> > tmp : edges) {
            edgesList.add(new Pair<Node, Node>(new Node(tmp.get("key")), new Node(tmp.get("value"))));
        }
    }

    /**
     * load crawled edges into the graph
     * @throws IOException
     */
    public void load() throws IOException {
        Stream<Path> stream = Files.list(Paths.get("data/crawled"));
        Set<String> files = stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName).map(Path::toString).collect(Collectors.toSet());
        edgesList = new ArrayList<>();
        for (String file : files) {
            loadFromFile(file);
        }
        System.out.println("Graph loaded! :))");
    }

    public Set<Node> getNodeList() {
        TreeSet<Node> nodeList = new TreeSet<>(new NodeComparator());
        for (Pair<Node, Node> edge : edgesList) {
            nodeList.add(edge.getKey());
            nodeList.add(edge.getValue());
        }
        return nodeList;
    }

    public void visualize() {
        // Construct graph
        int idx = 0;
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        Map<Node, String> vertexStringMapper = new TreeMap<>(new NodeComparator());
        Map<String, Object> vertexObjMapper = new TreeMap<>(new StringComparator());
        Set<Node>NodeSet = getNodeList();
        for (Node node : NodeSet) {
            if (node.isUser()) {
//                vertexStringMapper.put(node, graph.addVertex(node.getUser()));
                vertexStringMapper.put(node, node.getUser());
                graph.addVertex(node.getUser());
            }
            else {
                idx++;
//                vertexStringMapper.put(node, graph.addVertex(Str.itos(idx))); ;
                vertexStringMapper.put(node, Str.itos(idx));
                graph.addVertex(Str.itos(idx));
            }
        }
        for (Pair<Node, Node> edge : edgesList) {
            graph.addEdge(vertexStringMapper.get(edge.getKey()), vertexStringMapper.get(edge.getValue()));
        }
        // Create a new graph for visualization
        mxGraph mxGraph = new mxGraph();
        Object parent = mxGraph.getDefaultParent();

        mxGraph.getModel().beginUpdate();
        try {
            for (String vertex : graph.vertexSet()) {
                vertexObjMapper.put(vertex, mxGraph.insertVertex(parent, null, vertex, 0, 0, 80, 30));
            }
            // Create edges
            for (DefaultEdge edge : graph.edgeSet()) {
                String source = graph.getEdgeSource(edge);
                String target = graph.getEdgeTarget(edge);
                mxGraph.insertEdge(parent, null, "", vertexObjMapper.get(source), vertexObjMapper.get(target), target);
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }

        // Create a layout
        mxCircleLayout layout = new mxCircleLayout(mxGraph);
        layout.execute(parent);

        // Create a Swing component to display the graph
        mxGraphComponent graphComponent = new mxGraphComponent(mxGraph);
        JFrame frame = new JFrame("Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphComponent);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }
}
