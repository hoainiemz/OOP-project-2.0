package std;

import grapheditor.GraphEditor;
import grapheditor.Grapher;
import twitter.CrawlOptions;
import twitter.Twitter;

import java.io.IOException;

public class Environment {
    public static void search() throws InterruptedException, IOException {
        CrawlOptions options = new CrawlOptions();
        Twitter agent = new Twitter(options);
        agent.search();
    }

    public static void crawl() throws InterruptedException, IOException {
        CrawlOptions options = new CrawlOptions();
        Twitter agent = new Twitter(options);
        agent.crawl();
    }

    public static void updateFollowingEdges() throws InterruptedException, IOException {
        CrawlOptions options = new CrawlOptions("https://x.com/");
        Twitter agent = new Twitter(options);
        agent.updateFollowingEdges();
    }

    public static void main(String[] args) throws InterruptedException, IOException{
    }
}
