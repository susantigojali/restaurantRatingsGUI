package model.RestaurantRatings;

import model.DataProcessing.Postprocess;
import model.DataProcessing.Preprocess;
import model.Evaluator.AspectAggregationEvaluator;
import model.Evaluator.ExtractionEvaluator;
import model.Learning.MyCRFSimpleTagger;
import model.Learning.WekaHelper;
import model.Model.*;
import cc.mallet.fst.CRF;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

/**
 *
 * @author susanti_2
 */
public class Main {

    private static final String NBC_MODEL = "datatest/Model/nbc AA 6F (1688 modif).model";
    private static final String J48_MODEL = "datatest/Model/J48 B - 7 Fitur (1696).model";
    private static final String CRF_MODEL = "datatest/Model/crfFULL2 new (992).model";

    private static final int SUBJECTIVE_INDEX = 0;
    private static final int FORMALIZED_SENTENCE_INDEX = 0;

    private static final String NEW_LINE_SEPARATOR = "\n";

    public static int INDEX = 0;
    /**
     * read model from file
     *
     * @param modelFilename model filename
     * @return CRF
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static CRF loadModelCRF(String modelFilename) throws IOException, ClassNotFoundException {
        ObjectInputStream s = new ObjectInputStream(new FileInputStream(modelFilename));
        CRF crf = (CRF) s.readObject();
        return crf;
    }

    /**
     * Prepare data for annotation (NBC Learning)
     */
    public static void prepareDataLearningNBC() {

        String rawFilename = "crawl-data/DataLearningNBC5 (185).csv";
        String dataNbc = "dataset/NBC/DataLearningNBC5 new (185) NBCAnotasi";
        try {
            ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);
            classifySentenceSubjectivity(reviews, dataNbc, true);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: prepare data learning NBC");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Prepare data for annotation (CRF Learning)
     * @throws java.io.IOException
     */
    public static void prepareDataLearningCRF() throws IOException {
        String rawCrfFilename = "dataset/CRF/rawdata5 (553).txt";
        String dataCRF = "dataset/CRF/CRFAnotasi5 (file temporary because of app).csv";
        Preprocess.preprocessDataforSequenceTagging(rawCrfFilename, dataCRF, false);

        Boolean includeInput = true;
        int nBestOption = 1;
        ArrayList<SequenceTagging> inputSequence = Reader.readSequenceInput(dataCRF);

        try {
            String dataCRFAnotasi = "datatest/CRF/CRFAnotasi5.csv";
            ArrayList<SequenceTagging> outputs = MyCRFSimpleTagger.myClassify(dataCRF, CRF_MODEL, includeInput, nBestOption, inputSequence);

            saveClassifyCRF(dataCRFAnotasi, outputs);

        } catch (FileNotFoundException ex) {
            System.out.println("file not found: data , model CRF");
        } catch (ClassNotFoundException ex) {
            System.out.println("class not found: model");
        }
    }

    //save to file CRF yang udah dilabelim
    private static void saveClassifyCRF(String filename, ArrayList<SequenceTagging> outputs) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            System.out.println("File sudah ada");
            System.exit(-1);
        }

        FileWriter fw = new FileWriter(file);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("sep=;" + NEW_LINE_SEPARATOR);
            for (SequenceTagging output : outputs) {
                for (int i = 0; i < output.getSequenceInput().size(); i++) {
                    bw.write(output.getSequenceInput().get(i).getWord() + ";" + output.getSequenceInput().get(i).getPostag() + ";");
                    bw.write(output.getOutput().get(i) + NEW_LINE_SEPARATOR);
                }
                bw.write(NEW_LINE_SEPARATOR);
            }
        }
    }

    public static void saveSubjectiveInstance(String filename, Instances instances) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            for (Instance inst : instances) {
                if (inst.classAttribute().value((int) inst.classValue()).equals(inst.attribute(inst.classAttribute().index()).value(SUBJECTIVE_INDEX))) {
                    bw.write(inst.stringValue(FORMALIZED_SENTENCE_INDEX) + NEW_LINE_SEPARATOR);
                }
            }
        }
    }

    public static void main(String args[]) {
        try {
//            prepareDataLearningNBC();
//            prepareDataLearningCRF();
//            startApp();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //for testing
    /**
     * run the application
     *
     */
    public static ArrayList<ReviewOutput> startApp(String filereview) {
        double fExtraction = 0.0 ;
        double precExtraction = 0.0 ;
        double recExtraction = 0.0 ;
        double fAgg = 0.0 ;
        double precAgg = 0.0 ;
        double recAgg = 0.0 ;


        ArrayList<ReviewOutput> reviewOutputs = new ArrayList<>();

        try {
            //for extraction evaluation
//            String dataExtractionAS = "datatest/CRF/CRFExtraction.txt";
//            ArrayList<ArrayList<AspectSentiment>> actualAspectSentiments = Reader.readActualAspectSentiment(dataExtractionAS);

            //for aggregation evaluation
//            String dataAspectAggregation = "datatest/CRF/AspectAggregation.txt";
//            ArrayList<LinkedHashMap<String, ArrayList<AspectSentiment>>> actualAspectAggregation = Reader.readAspectAggregation(dataAspectAggregation);

            //Start Application

            String rawFilename = "datatest/NBC/RawDatatest.txt";
            String dataNbcCsv = "datatest/NBC/NBCData";
            ArrayList<Review> reviews = Reader.readReviewFromFile(filereview);
            for (Review review:reviews) {
                System.out.println("======================================================================= "+INDEX);
                ArrayList<Review> reviewsTemp = new ArrayList<>();
                reviewsTemp.add(review);

                //for view
                ReviewOutput ro = new ReviewOutput();

                //Step 1 Subjectivity Classification
                Instances instances = classifySentenceSubjectivity(reviewsTemp, dataNbcCsv+INDEX, false);
                ro.setOutputNBC(instances);

                //Step 2
                String rawCrfFilename = "datatest/CRF/rawdata";//.txt";
                String dataCRF = "datatest/CRF/CRFData";//.txt";
                Boolean includeInput = true;
                int nBestOption = 1;

                try {
                    //Preprocess CRF
                    Preprocess.preprocessDataforSequenceTagging(rawCrfFilename+INDEX+".txt", dataCRF+INDEX+".txt", false);

                    //Klasifikasi CRF
                    ArrayList<SequenceTagging> inputSequence = Reader.readSequenceInput(dataCRF+INDEX+".txt");
                    ArrayList<SequenceTagging> outputs = MyCRFSimpleTagger.myClassify(dataCRF+INDEX+".txt", CRF_MODEL, includeInput, nBestOption, inputSequence);

                    //for view
                    ro.setOutputCRF(outputs);

                    System.out.println("\n==TOKEN CLASSIFICATION==");
                    //for debugging extraction
                    for (int i = 0; i < outputs.size(); i++) {
                        for (int j = 0; j < outputs.get(i).getSequenceInput().size(); j++) {
                             System.out.println(outputs.get(i).getSequenceInput().get(j).getWord() + " "+
                                    outputs.get(i).getSequenceInput().get(j).getPostag() + " "+
                                    outputs.get(i).getOutput().get(j));
                        }
                        System.out.println();

                    }

                    //Extract Aspect and Opinion
                    ArrayList<AspectSentiment> as = new ArrayList<>();
                    for (SequenceTagging output : outputs) {
                        as.addAll(Postprocess.findAspectSentiment(output));
                    }

                    //for view
                    ro.setAspectSentiments(as);

                    System.out.println("\n==ASPECT & SENTIMENT EXTRACTION==");
                    for (int j = 0; j < as.size(); j++) {
                        System.out.print(j + ":\t");
                        as.get(j).print();
                    }

                    //Evaluation extraction aspect and sentiment
//                    ExtractionEvaluator.evaluate(as, actualAspectSentiments.get(INDEX));
//                    ExtractionEvaluator.printEvaluation();
//                    fExtraction+=ExtractionEvaluator.getF1();
//                    precExtraction+=ExtractionEvaluator.getPrecision();
//                    recExtraction+=ExtractionEvaluator.getRecall();

                    //Agregasi Aspek
//                    System.out.println("\n\nAgregasi Aspek-------------------------");
                    LinkedHashMap<String, ArrayList<AspectSentiment>> aggregation = AspectAggregation.aggregation(as);
                    System.out.println("\n\nTotal Category :"+ aggregation.size());

                    //for view
                    LinkedHashMap<String, Double> ratings = new LinkedHashMap<>();

                    for (String key : aggregation.keySet()) {
                        ArrayList<AspectSentiment> value = aggregation.get(key);
                        double rating = Rating.getRating(value);
                        System.out.println(key + " ========= rating: " + rating);
                        for (int i = 0; i < value.size(); i++) {
                            System.out.print("\t");
                            value.get(i).print();
                        }

                        //for view
                        ratings.put(key, rating);
                    }

                    //for view
                    ro.setAggregation(aggregation);
                    ro.setRating(ratings);

                    //Evaluation aspect aggregation
//                    AspectAggregationEvaluator.evaluate(aggregation, actualAspectAggregation.get(INDEX));
//                    AspectAggregationEvaluator.printEvaluation();
//                    fAgg+=AspectAggregationEvaluator.getF1();
//                    precAgg+=AspectAggregationEvaluator.getPrecision();
//                    recAgg+=AspectAggregationEvaluator.getRecall();

                    //for view
                    reviewOutputs.add(ro);

                } catch (IOException ex) {
                    System.out.println("IO Exeption");
                } catch (ClassNotFoundException ex) {
                    System.out.println("Class not found exception");
                }

                INDEX++;
            }

//            System.out.println("Extraction::");
//            System.out.println("prec:  "+precExtraction/reviews.size());
//            System.out.println("rec: " + recExtraction/reviews.size());
//            System.out.println("f1: " + fExtraction/reviews.size());
//
//            System.out.println("Aggregation::");
//            System.out.println("prec:  "+precAgg/reviews.size());
//            System.out.println("rec: " + recAgg/reviews.size());
//            System.out.println("f1: " + fAgg/reviews.size());

            INDEX =0;

            return reviewOutputs;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Instances classifySentenceSubjectivity(ArrayList<Review> reviews, String dataNbc, boolean isLearning) {
        //Step 1 Subjectivity Classification
        //String rawFilename = "datatest/NBC/RawDatatest.txt";
        try {
            //Read review from file
            //ArrayList<Review> reviews = Reader.readReviewFromFile(rawFilename);

            //Preprocess
            String dataNbcCsv = dataNbc + ".csv";
            String dataNbcArff = dataNbc + "(delete this).arff";
            Preprocess.preprocessDataforNBC(reviews, dataNbcCsv);
            Csv2Arff.convert(dataNbcCsv, dataNbcArff);

            //load ARFF datatest
            Instances dataTest = WekaHelper.loadDataFromFile(dataNbcArff);
            dataTest.setClassIndex(1);

            //load model
            NaiveBayes NB = (NaiveBayes) WekaHelper.loadModelFromFile(NBC_MODEL);
            J48 J48 = (J48) WekaHelper.loadModelFromFile(J48_MODEL);
//            System.out.println(NB.getHeader());

            //Classification
            Instances unlabeledData = WekaHelper.removeAttribute(dataTest, "1"); //remove attribute formalized_sentence

            System.out.println("\n==SUBJECTIVE SENTENCE==");
            for (int i = 0; i < unlabeledData.size(); i++) {
                double label = NB.classifyInstance(unlabeledData.instance(i));
//                double label = J48.classifyInstance(unlabeledData.instance(i));
                unlabeledData.instance(i).setClassValue(label);
                dataTest.instance(i).setClassValue(label);

                //print subjective sentence
                if ( dataTest.classAttribute().value((int) dataTest.instance(i).classValue()).equals(dataTest.attribute(dataTest.instance(i).classAttribute().index()).value(SUBJECTIVE_INDEX))) {
                    System.out.println(dataTest.instance(i).stringValue(FORMALIZED_SENTENCE_INDEX));
                }
            }

//            System.out.println("DATATEST=="); //for debugging
//            DataSink.write(System.out, dataTest); //for debugging

            if (!isLearning) {
                //Save data with label subjective
                String filename = "datatest/CRF/rawdata"+INDEX+".txt";
                saveSubjectiveInstance(filename, dataTest);
            } else {
                //for annotation to get the label
                printLabel(dataTest);
            }

            return dataTest;

        } catch (FileNotFoundException ex) {
            System.out.println("File not found exception");
        } catch (IOException ex) {
            System.out.println("IO Exception");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }



    private static void printLabel(Instances instances) {
        System.out.println("\nLabel==========");
        for (Instance inst : instances) {
            System.out.println(inst.classAttribute().value((int) inst.classValue()));
        }
    }
}
