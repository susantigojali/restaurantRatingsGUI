package model.Model;

import weka.core.Instances;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ReviewOutput {

    private Instances outputNBC;
    private ArrayList<SequenceTagging> outputCRF;
    private ArrayList<AspectSentiment> aspectSentiments;
    private LinkedHashMap<String, ArrayList<AspectSentiment>> aggregation;
    private LinkedHashMap<String, Double> rating;

    public ReviewOutput() {
    }

    public Instances getOutputNBC() {
        return outputNBC;
    }

    public void setOutputNBC(Instances dataTest) {
        this.outputNBC = dataTest;
    }

    public ArrayList<SequenceTagging> getOutputCRF() {
        return outputCRF;
    }

    public void setOutputCRF(ArrayList<SequenceTagging> outputs) {
        this.outputCRF = outputs;
    }

    public ArrayList<AspectSentiment> getAspectSentiments() {
        return aspectSentiments;
    }

    public void setAspectSentiments(ArrayList<AspectSentiment> aspectSentiments) {
        this.aspectSentiments = aspectSentiments;
    }

    public LinkedHashMap<String, ArrayList<AspectSentiment>> getAggregation() {
        return aggregation;
    }

    public void setAggregation(LinkedHashMap<String, ArrayList<AspectSentiment>> aggregation) {
        this.aggregation = aggregation;
    }

    public LinkedHashMap<String, Double> getRating() {
        return rating;
    }

    public void setRating(LinkedHashMap<String, Double> rating) {
        this.rating = rating;
    }
}
