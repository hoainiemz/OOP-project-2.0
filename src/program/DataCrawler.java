package program;

import definitions.Constants;
import graph.ActionGraph;
import jsonhandler.JsonHandler;
import twittercrawler.CrawlOptions;
import twittercrawler.CrawlAgent;

import java.io.IOException;
import java.util.ArrayList;

public class DataCrawler {
    public static void search() throws InterruptedException, IOException {
        CrawlOptions options = new CrawlOptions();
        CrawlAgent agent = new CrawlAgent(options);
        agent.search();
    }

    public static void crawl() throws InterruptedException, IOException {
        CrawlOptions options = new CrawlOptions();
        CrawlAgent agent = new CrawlAgent(options);
        agent.crawl();
        JsonHandler.dumpToJSON(new ArrayList<Object>(), Constants.SKIPPED_PATH);
    }

    public static void updateFollowingEdges() throws InterruptedException, IOException {
        CrawlOptions options = new CrawlOptions("https://x.com/");
        CrawlAgent agent = new CrawlAgent(options);
        agent.updateFollowingEdges();
        JsonHandler.dumpToJSON(new ArrayList<Object>(), Constants.SKIPPED_PATH);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        search();
//        crawl();
//        updateFollowingEdges();
//        ActionGraph graph = new ActionGraph();
//        graph.load();
//        System.out.println(graph.getEdgesList().size());
    }
}
