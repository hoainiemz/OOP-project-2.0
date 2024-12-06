package graph;

import std.StringFunction;
import twittercrawler.CrawlOptions;

import java.util.LinkedHashMap;

public class Node implements Comparable<Node> {
    private String user;
    private  String xpath;

    public Node(LinkedHashMap<String, String> src) {
        this.user = src.get("user");
        this.xpath = src.get("xpath");
    }

    public Node() {
        user = "";
        xpath = "";
    }

    public Node(String user) {
        if (user.charAt(0) == '@') {
            user = user.substring(1);
        }
        this.user = user;
        xpath = "";
    }

    public Node(String user, String xpath) {
        if (user.charAt(0) == '@') {
            user = user.substring(1);
        }
        this.user = user;
        this.xpath = xpath;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public boolean isUser() {
        return xpath.compareTo("") == 0;
    }

    public String getUrl(CrawlOptions crawlOptions) {
        if (isUser()) {
            return crawlOptions.getUrl() + user;
        }
        return crawlOptions.getUrl() + user + "/status/" + xpath;
    }

    public static Node constructFromTweetUrl(String url) {
        String name = null, xpath = null;
        int u = url.lastIndexOf('/');
        xpath = url.substring(u + 1);
        int l = StringFunction.nthIndexOf(url, '/', 3);
        int r = url.indexOf('/', l + 1);
        name = url.substring(l + 1, r);
        return new Node(name, xpath);
    }

    public String getJSONFilename() {
        return "crawled/" + this.getUser() + ".json";
    }

    @Override
    public int compareTo(Node rhs) {
        int val = this.user.compareTo(rhs.user);
        if(val != 0) return val;
        return this.xpath.compareTo(rhs.xpath);
    }
}
