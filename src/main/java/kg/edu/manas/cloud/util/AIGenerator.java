package kg.edu.manas.cloud.util;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import java.util.Random;

public class AIGenerator {
    public static void build() throws Exception {

        ConverterUtils.DataSource source = new ConverterUtils.DataSource("heart-disease.arff");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(2);
        dataset.randomize(new Random());
        int trainSize = (int) Math.round(dataset.numInstances() * 0.6);
        int testSize = dataset.numInstances() - trainSize;
        Instances trainSet = new Instances(dataset, 0, trainSize);
        Instances testSet = new Instances(dataset, trainSize, testSize);

        Logistic model = new Logistic();
//        RandomForest model = new RandomForest();

        model.buildClassifier(trainSet);

        SerializationHelper.write("src/main/resources/heart-disease-prediction.model", model);

        Evaluation eval = new Evaluation(testSet);
        eval.evaluateModel(model, testSet);

        System.out.println("Correctly Classified Instances: " + eval.pctCorrect() + "%");
        System.out.println("Incorrectly Classified Instances: " + eval.pctIncorrect() + "%");
        System.out.println("Precision: " + eval.precision(1));
        System.out.println("Recall: " + eval.recall(1));
        System.out.println("F1 Score: " + eval.fMeasure(1));
        System.out.println(eval.toMatrixString());
    }
}
