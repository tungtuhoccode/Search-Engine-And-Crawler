package ProjectFiles;

import ProjectTesterFiles.Fruits1Tester.Fruits1SearchTester;
import ProjectTesterFiles.SearchResult;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Search {
    public static void main(String args[]){
        Search searcher = new Search();
        List<SearchResult> results = searcher.search("peach papaya",true,10);
        for (SearchResult p: results){
            System.out.println(p.getTitle());
            System.out.println(p.getScore());
        }
        List<SearchResult> results2 = searcher.search("peach papaya",false,10);
        for (SearchResult p: results2){
            System.out.println(p.getTitle());
            System.out.println(p.getScore());
        }
        results = searcher.search("peach papaya",true,1000);

        results = searcher.search("banana beach bana peach papaya",true,1000);
    }

    private String[] query;
    private HashMap<String, PageSearchData> allPagesData;
    private HashMap<String, Double> uniqueWordsIdf;
    private int totalWords;
    public DirectoryHelper dirHelper;
    private SearchData searchData;
    private List<SearchResult> searchResults;

    //constructor
    public Search() {
        dirHelper = new DirectoryHelper();
        searchData = new SearchData(true,dirHelper);
        loadUniqueWordsIdf();
        allPagesData = new HashMap<String, PageSearchData>();
        loadPagesdata(allPagesData);
        searchResults = new ArrayList<SearchResult>();
    }

    public void loadUniqueWordsIdf() {
        try {
            uniqueWordsIdf = new HashMap<String, Double>();
            BufferedReader readAllWords = new BufferedReader(new FileReader(dirHelper.getUniqueWordsFile()));

            while (true) {
                String wordLine = readAllWords.readLine();

                if (wordLine == null) {
                    break;
                }

                String word = wordLine.strip();
                double idf = searchData.getIdf(word);
                uniqueWordsIdf.put(word,idf);
            }

            readAllWords.close();
        } catch (FileNotFoundException e) {
            System.out.println("unique_words.txt File not found: computePagesTfidf");

        } catch (IOException i) {
            System.out.println("IOException computePagesTfidf");
        }

    }

    public List<SearchResult> search(String initQuery,boolean boost, int X) {
        System.out.println("start search");
        long startTime = System.nanoTime();
        totalWords = 0;
        this.query = initQuery.split(" ");
        HashMap<String, Double> queryWords= new HashMap<String, Double>();

        for(String wordInQuery: query){
            totalWords++;
            if (uniqueWordsIdf.containsKey(wordInQuery)){
                if(!queryWords.containsKey(wordInQuery)){
                    queryWords.put(wordInQuery,1.0);
                }

                else{
                    queryWords.put(wordInQuery, queryWords.get(wordInQuery) + 1);
                }
            }
        }
//        System.out.println();
//        System.out.println("Total number of words: "+totalWords);
//        System.out.println("Start of Query HashMap");

        for (String wordInQuery: queryWords.keySet()){
            double wordFrequency = queryWords.get(wordInQuery);
            queryWords.put(wordInQuery,calculateTfIdfForQuery(wordInQuery,wordFrequency));

        }

        double leftDenominator =0;
        for (String curWord: queryWords.keySet()) {
            leftDenominator += queryWords.get(curWord) * queryWords.get(curWord);
        }
        leftDenominator = Math.sqrt(leftDenominator);

        TreeSet<Page> pages = loadPages(queryWords,leftDenominator,searchData,boost);

        ArrayList<SearchResult> results = new ArrayList<SearchResult>();

        int i =0;
        for (Page p: pages){
            results.add(p);
            i++;
            if(i==X){
                break;
            }
        }
        pages.clear();

        long endTime = System.nanoTime();
        System.out.printf("search take: %.3fms\n",((endTime-startTime)/1000000.0));
        searchResults = results;
        return results;

    }

    public void loadPagesdata(HashMap<String, PageSearchData> allOtherPagesDataIn){
        try{
            BufferedReader readALlUrl = new BufferedReader(new FileReader(dirHelper.getAllIndexUrlMapFile()));
            while(true){
                String line = readALlUrl.readLine();
                if(line == null){
                    break;
                }
                String url = line.strip().split(" ")[1];
                PageSearchData otherPageData = new PageSearchData(url, dirHelper,searchData);
                allOtherPagesDataIn.put(url,otherPageData);
            }

        }
        catch (FileNotFoundException f){
            System.out.println("File not found in loadPages()");
        }
        catch (IOException e){
            System.out.println("IO exception in loadPages()");
        }
    }

    public TreeSet<Page> loadPages(HashMap<String,Double> queryVector, double left_denominator,SearchData searchData,boolean boost){
            TreeSet<Page> listOfPages = new TreeSet<Page>();
            for (String url: allPagesData.keySet()){
                Page curPage = new Page(allPagesData.get(url));
                curPage.calculateCosineSimilarity(queryVector,left_denominator,boost);
                listOfPages.add(curPage);
            }
            return listOfPages;

    }
    public double calculateTfIdfForQuery(String word, double frequency){
        double tf = 1 + frequency/totalWords;
        double idf = uniqueWordsIdf.get(word);
        return Math.log(tf) / Math.log(2) * idf;

    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }
}
