package test;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class CMPT456Similarity extends ClassicSimilarity {
    //rewrite the method
    @Override
    public float tf(float freq){
        return (float)Math.sqrt(1+freq);
    }
    @Override
    public float idf(long docFreq, long docCount) {
        return (float)(Math.log((docCount+2)/(double)(docFreq+2)) + 1.0);
    }


}