package ProjectFiles;

import java.text.DecimalFormat;
import java.util.HashMap;

import ProjectTesterFiles.SearchResult;
public class Page implements SearchResult, Comparable<Page>{
    private double cosineSimilarity;
    private PageSearchData pageData;

    public Page(PageSearchData pageDataIn){
        this.cosineSimilarity = -1;
        this.pageData = pageDataIn;
    }

    public void calculateCosineSimilarity(HashMap<String,Double> queryVector, double left_denominator, boolean boost){
        Double rightDenom = 0.0;
        Double numerator = 0.0;
        for (String word: queryVector.keySet()){
            double curPageWordTfIdf = pageData.getTfIdf(word);

            rightDenom += curPageWordTfIdf*curPageWordTfIdf;

            numerator += queryVector.get(word)*curPageWordTfIdf;
        }

        rightDenom = Math.sqrt(rightDenom);
        if (rightDenom*left_denominator == 0){
            this.cosineSimilarity = 0;
        }
        else{
            this.cosineSimilarity = numerator/ (rightDenom*left_denominator);
        }
        if (boost){
            cosineSimilarity *= pageData.getPageRank();
        }
    }

    @Override
    public int compareTo(Page p) {
        DecimalFormat df = new DecimalFormat("#.###");
        double thisPageScoreRounded = Math.round (this.getScore()*1000);
        double parameterPageScoreRounded =  Math.round (p.getScore()*1000);
        if(parameterPageScoreRounded>thisPageScoreRounded){
            return 1;
        } else if (thisPageScoreRounded>parameterPageScoreRounded) {
            return -1;
        }

        return this.pageData.getTitle().compareTo(p.getTitle());
    }

    public String getTitle() {
        return pageData.getTitle();
    }

    public double getScore() {
        return cosineSimilarity;
    }

    @Override
    public String toString() {
        return String.format("Page Name: %s %15s\t Score: %.10f", getTitle(), " ", getScore());
    }
}
