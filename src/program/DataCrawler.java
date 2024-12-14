package program;

import twittercrawler.CrawlOptions;
import twittercrawler.CrawlAgent;

import java.io.IOException;

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
    }

    public static void updateFollowingEdges() throws InterruptedException, IOException {
        CrawlOptions options = new CrawlOptions("https://x.com/");
        CrawlAgent agent = new CrawlAgent(options);
        agent.updateFollowingEdges();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
//        search();
        crawl();
    }
}
