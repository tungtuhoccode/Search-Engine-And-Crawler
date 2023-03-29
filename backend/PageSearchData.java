package ProjectFiles;

import java.io.*;
import java.util.HashMap;

public class PageSearchData {
    private String url;
    private HashMap<String, Double> uniqueWordsTfIdf;
    private double pageRank;
    private String title;

    //constructor
    public PageSearchData(String urlIn, DirectoryHelper dirHelper, SearchData data1){
        this.title = data1.getTitle(urlIn);
        this.url = urlIn;
        uniqueWordsTfIdf = new HashMap<String, Double>();
        try{
            BufferedReader readAllWords = new BufferedReader(new FileReader(dirHelper.getUniqueWordsFile()));

            while (true) {
                String wordLine = readAllWords.readLine();

                if (wordLine == null) {
                    break;
                }
                String word = wordLine.strip();
                Double tfidf = data1.getTfIdf(url,word);

                uniqueWordsTfIdf.put(word,tfidf);
            }
        }
        catch (FileNotFoundException e){
            System.out.println("File not found exception in OtherPageData");
        }
        catch (IOException e){
            System.out.println("IOException exception in OtherPageData");
        }

        this.pageRank = data1.getPageRank(url);
    }

    public String toString(){
        String result ="";
        for (String word: uniqueWordsTfIdf.keySet()){
            result += word+": "+uniqueWordsTfIdf.get(word)+"\n";
        }
        return "Url is: "+url+"\nPageRank is "+pageRank+"\n"+result;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public double getTfIdf(String word){
        return uniqueWordsTfIdf.get(word);
    }
    public double getPageRank(){
        return pageRank;
    }
}
