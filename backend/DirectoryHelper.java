package ProjectFiles;

import java.io.*;
import java.util.HashMap;

public class DirectoryHelper {
    private HashMap<String, String> indexUrlMap = new HashMap<String,String>();
    private static int numbInstances = 0;
    public void setupInitialFolder(){
        removeAllFiles(new File(getCrawlDataPath()));

        //create directory accordingly
        new File(getCrawlDataPath()).mkdir();
        new File(getOutUrlFolderPath()).mkdir();
        new File(getInUrlFolderPath()).mkdir();
        new File(getTfFolder()).mkdir();
        new File(getIdfFolder()).mkdir();
        new File(getTfIdfFolder()).mkdir();
        new File(getTitleFolder()).mkdir();
        new File(getPageRankFolder()).mkdir();
    }

    public boolean isUrlExist(String url){
        return indexUrlMap.containsKey(url);
    }

    //Constructor
    public DirectoryHelper(){
        numbInstances++;
        System.out.println("new directoryHelper created");
        loadIndexMap();
    }

    public static int getNumbInstances() {
        return numbInstances;
    }

    public DirectoryHelper(boolean forCrawler){
        System.out.println("new directoryHelper created for crawler");
    }


    //Initialize Index Url Map
    public void printIndexMap() {
        for(String key: indexUrlMap.keySet()){
            System.out.println(key +": "+indexUrlMap.get(key));
        }
    }
    public void loadIndexMap(){
        try{
            BufferedReader readIndexMap = new BufferedReader(new FileReader(getAllIndexUrlMapFile()));

            while(true){
                String line = readIndexMap.readLine();
                if(line == null){
                    break;
                }

                String splitLine[] = line.split(" ");
                String url = splitLine[1];
                String index = splitLine[0];
                indexUrlMap.put(url,index);

            }

            readIndexMap.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Load Index Map Unsuccessful");
        }
        catch (IOException e){
            System.out.println("IO exception DirectoryHelper Constructor");
        }
    }


    //PAGERANK
    public String getPageRankFolder(){
        return getCrawlDataPath() + File.separator + "pagerank";
    }
    public String getPageRankFile(String url){
        return getPageRankFolder() + File.separator + getUrlFileName(url) +"-pagerank.txt";
    }
    public void writePageRankFile(String url, double pageRankScore){
        try{
            BufferedWriter pageRankWriter = new BufferedWriter(new FileWriter(getPageRankFile(url)));
            pageRankWriter.write(pageRankScore+"");
            pageRankWriter.close();
        }
        catch(FileNotFoundException f){
            System.out.println("File not found exception in writePageRank()");
        }
        catch (IOException e){
            System.out.println("IOException in writePagerank()");
        }
    }


    //ADD INDEX
    public void addToIndexMap(String url, Integer index){
        appendIndexUrlMap(index,url);
        indexUrlMap.put(url,Integer.toString(index));
    }
    public String getUrlFileName(String url){
        return indexUrlMap.get(url);
    }


    //REMOVE FILE
    public void removeAllFiles(File directory) {
        File[] directoryContent = directory.listFiles();

        if (directoryContent != null){
            for (File content: directoryContent){
                removeAllFiles(content);
            }
        }

        directory.delete();
    }

    //Title
    public String getTitleFolder(){
        return getCrawlDataPath() + File.separator + "title";
    }
    public String getTitleFile(String url){
        return getTitleFolder() + File.separator + getUrlFileName(url)+ ".txt";
    }
    public void writeTitleToFile(String url,String title){
        try{
            BufferedWriter titleWriter = new BufferedWriter(new FileWriter(getTitleFile(url)));
            titleWriter.write(title);
            titleWriter.close();
        }
        catch(IOException e){
            System.out.println("IO exception getTitleFile");
        }
    }

    //URL FILE NAME
//    public String getUrlFileName(String url){
//        String[] splittedUrl = url.split("/");
//        String urlFileName = "";
//        splittedUrl[splittedUrl.length-1] = splittedUrl[splittedUrl.length-1].substring(0,splittedUrl[splittedUrl.length-1].length()-5);
//
//        for (int i=1;i<splittedUrl.length-1;i++){
//            urlFileName += splittedUrl[i]+"_";
//        }
//
//        urlFileName += splittedUrl[splittedUrl.length-1];
//        return urlFileName;
//    }


    //OS PATH & CRAWL PATH
    public String getOSPath(){
        return  System.getProperty("user.dir");
    }
    public String getCrawlDataPath(){
        String CrawlDataName = "crawl_data";
        return getOSPath() + File.separator + CrawlDataName;
    }

    //TFIDF
    public String getTfIdfFolder(){
        return getCrawlDataPath() + File.separator + "tfidf";
    }
    public String getTfIdfPageFolder(String url){
        return getTfIdfFolder() + File.separator + getUrlFileName(url);
    }
    public String getTfIdfWordFile(String url, String word){
        return getTfIdfPageFolder(url) + File.separator +word+".txt";
    }
    public void writeTfIdfToFile(String url, String word, double tfidf){
        File pageFolder = new File(getTfIdfPageFolder(url));
        if (!pageFolder.exists()){
            pageFolder.mkdir();
        }

        String filePath = getTfIdfWordFile(url,word);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(""+tfidf);
            writer.close();

        }
        catch (IOException e){
            System.out.println("writeTfidfToFolder(String curUrl): IOException");
        }
    }

    //IDF
    public String getIdfFolder(){
        return getCrawlDataPath() + File.separator + "idf";
    }
    public String getIdfFile(String word){
        return getIdfFolder() + File.separator +word+".txt";
    }
    public void writeIdfToFile(String word, double idf){
        String filePath = getIdfFolder()+File.separator+ word+ ".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(""+idf);
            writer.close();

        }
        catch (IOException e){
            System.out.println("writeTfToFolder(String curUrl): IOException");
        }
    }


    //TF
    public String getTfFolder(){
        return getCrawlDataPath() + File.separator + "tf";
    }
    public void writeTfToFolder(String url, String word, double tf){
        File urlFolder = new File(getTfFolder() + File.separator + getUrlFileName(url));
        if(!urlFolder.exists()){
            if(urlFolder.mkdir()){
            }
        }
        String filePath = urlFolder.getAbsolutePath()+ File.separator + word+".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(""+tf);
            writer.close();

        }
        catch (IOException e){
            System.out.println("writeTfToFolder(String curUrl): IOException");
        }

    }
    public String getTfFileForWord(String url, String word){
        File urlFolder = new File(getTfFolder() + File.separator + getUrlFileName(url));
        String filePath = urlFolder.getAbsolutePath()+ File.separator + word+".txt";
        return filePath;
    }


    //Incoming links
    public String getInUrlFolderPath(){
        return getCrawlDataPath() + File.separator + "incoming";
    }
    public String getInUrlFilePath(String url){
        return getInUrlFolderPath() + File.separator + getUrlFileName(url)+"-incoming.txt";
    }
    public void writeIncomingLink(String inUrl, String curLink){
        try{

            BufferedWriter writeOutLink =  new BufferedWriter(new FileWriter(getInUrlFilePath(inUrl),true));
            writeOutLink.write(curLink+"\n");
            writeOutLink.close();
        }
        catch (IOException e){
            System.out.println("writeIncomingLink(String curUrl): IOException");
        }
    }

    //Outgoing links
    public String getOutUrlFolderPath(){
        return getCrawlDataPath() + File.separator + "outgoing";
    }
    public String getOutUrlFilePath(String url){
        return getOutUrlFolderPath() + File.separator + getUrlFileName(url)+"-outgoing.txt";
    }
    public void writeOutgoingLink(String curUrl, String outLinks){
        try{

            BufferedWriter writeOutLink =  new BufferedWriter(new FileWriter(getOutUrlFilePath(curUrl),true));
            writeOutLink.write(outLinks+"\n");
            writeOutLink.close();
        }
        catch (IOException e){
            System.out.println("writeOutgoingLink(String curUrl): IOException");
        }
    }


    //UNIQUE WORDS
    public String getUniqueWordsFile(){
        return getCrawlDataPath() + File.separator + "unique_words.txt";
    }
    public void writeUniqueWords(String word){
        try{
            //write to one file
            BufferedWriter writeUniqueWord = new BufferedWriter(new FileWriter(getUniqueWordsFile(),true));
            writeUniqueWord.write(word+"\n");
            writeUniqueWord.close();

        }
        catch (IOException e){
            System.out.println("IOException e");
        }
    }

    //Index Map
    public String getAllIndexUrlMapFile(){
        String indexUrlMapName = "index_url_map.txt";
        return getCrawlDataPath()+ File.separator + indexUrlMapName;
    }
    public void appendIndexUrlMap(int index,String url){
        try{
            //write to one file
            BufferedWriter writeIndexToFile = new BufferedWriter(new FileWriter(getAllIndexUrlMapFile(),true));
            writeIndexToFile.write(index+ " "+url+"\n");
            writeIndexToFile.close();

        }
        catch (IOException e){
            System.out.println("IOException e");
        }

    }

    public HashMap<String, String> getIndexUrlMap() {
        return indexUrlMap;
    }
}
