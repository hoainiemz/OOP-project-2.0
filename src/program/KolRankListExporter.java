package program;

import definitions.Constants;

import graph.ActionEdge;
import graph.ActionGraph;
import graph.Node;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import pagerank.DirectedEdge;
import pagerank.WeightedPageRankRunner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.TableView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;

class KolRankListTableRow implements Comparable<KolRankListTableRow> {
    public int rank;
    public String handle;
    public double point;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public KolRankListTableRow() {
        setRank(-1);
        setHandle("");
        setPoint(0);
    }
    public KolRankListTableRow(int rank, String handle, double point) {
        setRank(rank);
        setHandle(handle);
        setPoint(point);
    }

    public StringProperty rankProperty() {
        return new SimpleStringProperty(Integer.valueOf(getRank()).toString());
    }

    public StringProperty handleProperty() {
        return new SimpleStringProperty(getHandle());
    }

    public StringProperty pointProperty() {
        return new SimpleStringProperty(String.format("%5f", getPoint()));
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

public class KolRankListExporter extends Application {
    static void viewTable(Stage primaryStage, ArrayList<KolRankListTableRow> tableRowList) {
        BorderPane root = new BorderPane();
        TableView table = new TableView<KolRankListTableRow>();

        TableColumn<KolRankListTableRow, String> firstColumn = new TableColumn<KolRankListTableRow, String> ("Rank");
        firstColumn.setCellValueFactory(p -> p.getValue().rankProperty());

        TableColumn<KolRankListTableRow, String> secondColumn = new TableColumn<KolRankListTableRow, String> ("Handle");
        secondColumn.setCellValueFactory(p -> p.getValue().handleProperty());

        TableColumn<KolRankListTableRow, String> thirdColumn = new TableColumn<KolRankListTableRow, String> ("Point");
        thirdColumn.setCellValueFactory(p -> p.getValue().pointProperty());

        table.getColumns().add(firstColumn);
        table.getColumns().add(secondColumn);
        table.getColumns().add(thirdColumn);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        for(KolRankListTableRow t : tableRowList) {
            table.getItems().add(t);
        }

        root.setCenter(table);

        Scene scene = new Scene(root, 500, 300);
        scene.getStylesheets().add("stylesheet.css");
        primaryStage.setTitle("KOL ranklist");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override public void start(Stage stage) throws IOException {
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
            table.add(new KolRankListTableRow(-1, nd.getUser(), points[index]));
        }
        table.sort(KolRankListTableRow::compareTo);
        int cnt = 0;
        for(KolRankListTableRow t : table) {
            ++cnt;
            t.setRank(cnt);
        }
        viewTable(stage, table);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
