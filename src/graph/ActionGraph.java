package graph;

import jsonhandler.JsonHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
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

    public void loadFromFile(String file) throws IOException {
        assert file.endsWith(".json");
        String currentUser = file.substring(0, file.length() - 5);
        ArrayList<LinkedHashMap<LinkedHashMap<String, String>, LinkedHashMap<String, String> >> edges = JsonHandler.loadArrayFromJSON("/crawled/" + file);
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
        Set<String> files = stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName).map(Path::toString).collect(Collectors.toSet());
        edgesList = new ArrayList<>();
        for (String file : files) {
            loadFromFile(file);
        }
        System.out.println("Graph loaded! :))");
    }
}
