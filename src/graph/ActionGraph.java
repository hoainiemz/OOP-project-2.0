package graph;

import jsonhandler.JsonHandler;
import std.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActionGraph {
    protected ArrayList<ActionEdge> edgesList;
    public ActionGraph() {
        edgesList = new ArrayList<>();
    }

    public ArrayList<ActionEdge> getEdgesList() {
        return edgesList;
    }

    public void addCommentEdge(Node u, Node v) {
        this.getEdgesList().add(new CommentEdge(u, v));
    }
    public void addRepostEdge(Node u, Node v) {
        this.getEdgesList().add(new RepostEdge(u, v));
    }
    public void addFollowEdge(Node u, Node v) {
        this.getEdgesList().add(new FollowEdge(u, v));
    }
    public void addTweetEdge(Node u, Node v) {
        this.getEdgesList().add(new TweetEdge(u, v));
    }

    public void loadFromFile(String filepath) throws IOException {
        Path path = Paths.get(filepath);
        String filename = path.getFileName().toString();
        assert filename.endsWith(".json");
        String currentUser = filename.substring(0, filename.length() - 5);

        ArrayList<LinkedHashMap<LinkedHashMap<String, String>, LinkedHashMap<String, String> >> edges = JsonHandler.loadArrayFromJSON(filepath);
        for (LinkedHashMap<LinkedHashMap<String, String>, LinkedHashMap<String, String> > tmp : edges) {
//            edgesList.add(new Pair<Node, Node>(new Node(tmp.get("key")), new Node(tmp.get("value"))));
            Node u = new Node(tmp.get("key"));
            Node v = new Node(tmp.get("value"));
            if (u.isUser()) {
                if (v.isUser()) {
                    // u follows v
                    edgesList.add(new FollowEdge(u, v));
                }
                else {
                    if (u.getUser().equals(v.getUser())) {
                        // u posted v
                        edgesList.add(new TweetEdge(u, v));
                    }
                    else {
                        // u commented to v or u reposted v
                        if (u.getUser().equals(currentUser)) {
                            // u reposted v
                            edgesList.add(new RepostEdge(u, v));
                        }
                        else {
                            // u commented v
                            edgesList.add(new CommentEdge(u, v));
                        }
                    }
                }
            }
            else {
                edgesList.add(new TweetEdge(u, v));
            }
        }
    }

    public void load() throws IOException {
        Stream<Path> stream = Files.list(Paths.get("data/crawled"));
        Set<String> files = stream.filter(file -> !Files.isDirectory(file)).map(Path::toString).collect(Collectors.toSet());
        for (String file : files) {
            loadFromFile(file);
        }
        // normalize edge list
//        TreeSet<ActionEdge> ts = new TreeSet<>(edgesList);
//        edgesList.clear();
//        edgesList.addAll(ts);
        System.out.println("Graph loaded! :))");
    }

    public void save(Node user) throws IOException {
        ArrayList<Pair<Node, Node>> edgesListPairNodeFormat = new ArrayList<>(edgesList);
        JsonHandler.dumpToJSON(edgesListPairNodeFormat, user.getJSONFilename());
    }

    public Set<Node> getNodeList() {
        TreeSet<Node> nodeList = new TreeSet<>();
        for (Pair<Node, Node> edge : edgesList) {
            nodeList.add(edge.getKey());
            nodeList.add(edge.getValue());
        }
        return nodeList;
    }

    public static boolean isCrawled(Node user) {
        String nodeJSON = user.getJSONFilename();
        return JsonHandler.exists(nodeJSON);
    }
}
