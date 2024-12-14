package graph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import org.jgrapht.*;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import std.Pair;
import std.StringFunction;
import std.StringComparator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ActionGraphVisualizer extends ActionGraph {
    public ActionGraphVisualizer() {
        super();
    }

    private static void visualizeGraph(Graph<String, DefaultEdge> graph) throws IOException {
        JGraphXAdapter<String, DefaultEdge> graphAdapter =
                new JGraphXAdapter<>(graph);
//        graphAdapter.setLabelsVisible(false);
        graphAdapter.getEdgeToCellMap().forEach((edge, cell) -> cell.setValue(null));
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("graph.png");
        ImageIO.write(image, "PNG", imgFile);
    }

    public void visualize() {
        // Construct graph
        int idx = 0;
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        Map<Node, String> vertexStringMapper = new TreeMap<>();
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
                vertexStringMapper.put(node, StringFunction.iToS(idx));
                graph.addVertex(StringFunction.iToS(idx));
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
