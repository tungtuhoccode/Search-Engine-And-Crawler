package ProjectFiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class Crawler {
    public static void main(String args[]) {
        Crawler myCrawler = new Crawler();
        myCrawler.crawl("http://people.scs.carleton.ca/~davidmckenney/tinyfruits/N-0.html");
    }

    private Queue<String> urlQueue;
    private HashMap<String, Boolean> urlHashMap;
    private HashMap<String, Integer> uniqueWordWithFrequency;
    private int curUrlIndex;
    private int mapingIndex = 0;

    private DirectoryHelper dirHelp;
    private PageRankAndVector calculate;

    public Crawler() {
        urlQueue = new LinkedList<String>();
        urlHashMap = new HashMap<String, Boolean>();
        uniqueWordWithFrequency = new HashMap<String, Integer>();
        curUrlIndex = 0;
        System.out.println("new directoryHelper will be created in crawler");
        dirHelp = new DirectoryHelper(true);
        dirHelp.setupInitialFolder();
    }



    public String getUrlBase(String url) {
        String[] splittedUrl = url.split("/");
        String baseUrl = "";
        for (int i = 0; i < splittedUrl.length - 1; i++) {
            baseUrl += splittedUrl[i] + "/";
        }
        return baseUrl;
    }

    //write down tf of each word for current url
    public void processWords(String curUrl, String html) {
        int totalWordsInPage = 0;
        HashMap<String, Integer> uniqueWordInPage = new HashMap<String, Integer>();

        String htmlC[] = html.split("<p>\n");

        ArrayList<String> htmlContent = new ArrayList<String>();

        for (int i = 1; i < htmlC.length; i++) {
            htmlContent.add(htmlC[i].split("</p>")[0]);
        }

        for (int i = 0; i < htmlContent.size(); i++) {
            String curP[] = htmlContent.get(i).split("\n");
            for (int j = 0; j < curP.length; j++) {

                String curWord = curP[j];
                totalWordsInPage++;

                if (!uniqueWordInPage.containsKey(curWord)) {
                    uniqueWordInPage.put(curWord, 1);
                } else {
                    uniqueWordInPage.put(curWord, uniqueWordInPage.get(curWord) + 1);
                }

            }
        }

        for (String key : uniqueWordInPage.keySet()) {
            if (!uniqueWordWithFrequency.containsKey(key)) {
                uniqueWordWithFrequency.put(key, 1);
            } else {
                uniqueWordWithFrequency.put(key, uniqueWordWithFrequency.get(key) + 1);
            }
            dirHelp.writeTfToFolder(curUrl, key, uniqueWordInPage.get(key) / (double) totalWordsInPage);
        }

    }

    //write down outgoing, incoming links and add new links to the queue
    public void writeOutgoingIncomingUrl(String url, String htmlStr) {
        String curUrlBase = getUrlBase(url);
        String[] html_for_links = htmlStr.split("\"");

        for (String str : html_for_links) {
            if (str.charAt(0) == '.') {
                String relativeOutUrl = str;
                String fullOutUrl = curUrlBase + relativeOutUrl.substring(2, str.length());

                //add new link to queue
                if (!urlHashMap.containsKey(fullOutUrl)) {
                    dirHelp.addToIndexMap(fullOutUrl,mapingIndex);
                    mapingIndex++;
                    urlHashMap.put(fullOutUrl, false);
                    urlQueue.add(fullOutUrl);
                }

                //write down outgoing link
                dirHelp.writeOutgoingLink(url, fullOutUrl);
                dirHelp.writeIncomingLink(fullOutUrl, url);

            }
        }
    }

    //write down title
    public void writeTitle(String url, String html) {
        String title = html.substring(html.indexOf("<title>") + 7, html.indexOf("</title>"));
        dirHelp.writeTitleToFile(url, title);
    }

    //call other method to perform calculation during each crawl
    public void processEachURL(String url) {
        try {
            String htmlStr = WebRequester.readURL(url);
            //write down the title
            writeTitle(url, htmlStr);

            //write outgoing incoming url
            writeOutgoingIncomingUrl(url, htmlStr);

            //tf, idf, total word,...
            processWords(url, htmlStr);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //main crawl method
    public void crawl(String url) {

        urlQueue.add(url);
        dirHelp.addToIndexMap(url,mapingIndex);
        mapingIndex++;
        urlHashMap.put(url, true);

        while (!urlQueue.isEmpty()) {
            System.out.println(curUrlIndex);
            String curUrl = urlQueue.peek();
            //map index to url
//            dirHelp.appendIndexUrlMap(curUrlIndex, curUrl);

            curUrlIndex++;
            System.out.println("Crawling " + curUrl);
            processEachURL(curUrl);
            System.out.println("Removed from queue: " + urlQueue.remove());

        }

        processAfterCrawled();
    }

    //calculation after crawl
    public void processAfterCrawled() {
        //calculate idf

        for (String word : uniqueWordWithFrequency.keySet()) {
            double numer = curUrlIndex;
            double denom = 1 + uniqueWordWithFrequency.get(word);
            double idf = Math.log(numer / denom) / Math.log(2);
            dirHelp.writeIdfToFile(word, idf);
            dirHelp.writeUniqueWords(word);
        }

        //generate page vector
        calculate = new PageRankAndVector();
        calculate.computePagesTfidf();
        //generate pagerank
        calculate.computePageRank();

        System.out.println("Number of instance created for dirHelper: "+dirHelp.getNumbInstances());

    }

}

