package program;

import java.util.TreeSet;

import graph.Node;
import graph.ActionEdge;
import graph.ActionGraph;
import std.Pair;

import java.io.IOException;
import java.util.*;

public class CrawlDataStatisticExporter {
    public static void main(String[] args) throws IOException {
        ActionGraph actionGraph = new ActionGraph();
        actionGraph.load();

        Set<Node> nodeList = actionGraph.getNodeList();
        ArrayList<ActionEdge> edgesList = actionGraph.getEdgesList();

        TreeSet<Node> kolList = kolLoader.loadKol();

        System.out.printf("So dinh: %d\n", nodeList.size());
        System.out.printf("So canh: %d\n", edgesList.size());

        int cntUser = 0;
        for (Node node : nodeList) {
            if (node.isUser()) {
                ++cntUser;
            }
        }
        System.out.printf("So user: %d\n", cntUser);
        System.out.printf("So kol: %d\n", kolList.size());

        int cntComment = 0;
        int cntRepost = 0;
        int cntFollow = 0;
        int cntTweet = 0;
        for(ActionEdge edge : edgesList) {
            if(edge.getClass() == graph.CommentEdge.class) {
                ++cntComment;
            }
            else if(edge.getClass() == graph.RepostEdge.class) {
                ++cntRepost;
            }
            else if(edge.getClass() == graph.FollowEdge.class) {
                ++cntFollow;
            }
            else {
                ++cntTweet;
            }
        }
        System.out.printf("So luot comment: %d\n", cntComment);
        System.out.printf("So luot repost: %d\n", cntRepost);
        System.out.printf("So luot follow: %d\n", cntFollow);
        System.out.printf("So luot tweet: %d\n", cntTweet);
    }
}

