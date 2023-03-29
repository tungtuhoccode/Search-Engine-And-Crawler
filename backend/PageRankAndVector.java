package ProjectFiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageRankAndVector {
    public static void main(String[] args) {
        PageRankAndVector test = new PageRankAndVector();
        test.computePageRank();
    }
    SearchData data1 = new SearchData();
    DirectoryHelper dirHelper = new DirectoryHelper();

    public void computePageRank(){
        //initialize the map
        HashMap<String, Integer> urlToIndex =  new HashMap<String, Integer>();
        HashMap<Integer, String> indexToUrl =  new HashMap<Integer, String>();
        int numbPage = 0;
        double alphaVal = 0.1;

        for(String key: dirHelper.getIndexUrlMap().keySet()){
            urlToIndex.put(key,Integer.parseInt(dirHelper.getIndexUrlMap().get(key)));
        }
        for (String key: urlToIndex.keySet()){
           indexToUrl.put(urlToIndex.get(key), key);
           numbPage++;
        }

        //DONE INITILIZING
//        System.out.println("Here is the INDEX to URL map in compute page rank");
//        for (String key: urlToIndex.keySet()){
//            System.out.println(key +": "+urlToIndex.get(key));
//        }
//        System.out.println("Here is the URL to INDEX map in compute page rank");
//        for (int key: indexToUrl.keySet()){
//            System.out.println(key +": "+indexToUrl.get(key));
//        }
//        System.out.println("number of pages: "+numbPage);
//
        //start pagerank calculation
        //START PAGE RANK CALCULATION

        double matrix[][] = new double[numbPage][numbPage];
        double [] numberOf1sArray = new double[numbPage];

        //Adjacency Matrix
        for (int pageIndex = 0;pageIndex<numbPage;pageIndex++){
            ArrayList<String> curPageOutgoingList = new ArrayList<String>(data1.getOutgoingLinks(indexToUrl.get(pageIndex)) );
            int numberOf1s = 0;
            for(String outGoingLink: curPageOutgoingList){
                matrix[pageIndex][urlToIndex.get(outGoingLink)] = 1;
                numberOf1s++;
            }
            numberOf1sArray[pageIndex] = numberOf1s;
        }

        //Initial transition probability matrix
        for (int pageIndex = 0;pageIndex<numbPage;pageIndex++){
          for (int outgoingIndex =0;outgoingIndex<numbPage;outgoingIndex++){
              if(numberOf1sArray[pageIndex]==0){
                  matrix[pageIndex][outgoingIndex]=1/numbPage;
              }
              else{
                  matrix[pageIndex][outgoingIndex]/=numberOf1sArray[pageIndex];
              }
          }
        }

        //Scaled Adjacency Matrix
        for (int pageIndex = 0;pageIndex<numbPage;pageIndex++){
            for (int outgoingIndex =0;outgoingIndex<numbPage;outgoingIndex++){
               matrix[pageIndex][outgoingIndex] *= (1-alphaVal);
            }
        }

        //Adding alpha/N
        for (int pageIndex = 0;pageIndex<numbPage;pageIndex++){
            for (int outgoingIndex =0;outgoingIndex<numbPage;outgoingIndex++){
                matrix[pageIndex][outgoingIndex] += alphaVal/numbPage;
            }
        }

        //New matrix
        double vectorPi[][] = new double[1][numbPage];
        double vectorPiValue = 1.0/numbPage;

        for (int i=0;i<numbPage;i++){
                vectorPi[0][i] = vectorPiValue;
        }

        double [][] previousResult = matMult(vectorPi,matrix);
        double [][] curResult = matMult(previousResult,matrix);

        while(true){
            if(euclidean_dist(previousResult,curResult)<0.0001){
                break;
            }

            previousResult = curResult;
            curResult = matMult(previousResult,matrix);
        }

        for (int pageIndex = 0;pageIndex<numbPage;pageIndex++){
                dirHelper.writePageRankFile(indexToUrl.get(pageIndex),curResult[0][pageIndex]);
        }

    }

    public double euclidean_dist(double[][] a, double[][] b){
        double squaredDist = 0.0;

        for (int i=0;i<a[0].length;i++){
            squaredDist += Math.pow((a[0][i]-b[0][i]),2);
        }
        return Math.sqrt(squaredDist);
    }
    public double[][] matMult(double[][] firstMatrix,double[][]secondMatrix){

        int firstMatrixColumns = firstMatrix[0].length;
        int firstMatrixRows = firstMatrix.length;
        int secondMatrixColumns = secondMatrix[0].length;
        int secondMatrixRows = secondMatrix.length;

        double returnList[][] = new double[firstMatrixRows][secondMatrixColumns];

        if (firstMatrixColumns == secondMatrixRows) {
            for (int i = 0; i < firstMatrix.length; i++) {
                for (int j = 0; j < secondMatrix[0].length; j++) {
                    for (int k = 0; k < firstMatrix[0].length; k++) {
                        returnList[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                    }
                }
            }
        }


        return returnList;

    }
    public void computePagesTfidf() {
        try {
            BufferedReader readAllUrl = new BufferedReader(new FileReader(dirHelper.getAllIndexUrlMapFile()));
            while (true) {
                String line = readAllUrl.readLine();
                if (line == null) {
                    break;
                }

                String url = line.strip().split(" ")[1];
                BufferedReader readAllWords = new BufferedReader(new FileReader(dirHelper.getUniqueWordsFile()));

                while (true) {
                    String wordLine = readAllWords.readLine();

                    if (wordLine == null) {
                        break;
                    }

                    String word = wordLine.strip();
                    double tf = 1 + data1.getTF(url, word);
                    double idf = data1.getIdf(word);
                    double tfidf = Math.log(tf) / Math.log(2) * idf;
                    dirHelper.writeTfIdfToFile(url, word, tfidf);
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println("unique_words.txt File not found: computePagesTfidf");
        } catch (IOException i) {
            System.out.println("IOException computePagesTfidf");
        }
    }

}
