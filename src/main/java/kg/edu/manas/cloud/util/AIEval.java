package kg.edu.manas.cloud.util;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import java.util.Random;

public class AIEval {
    public static void main(String[] args) throws Exception {
        build();
    }
    public static void build() throws Exception {

        ConverterUtils.DataSource source = new ConverterUtils.DataSource("heart-disease.arff");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(2);
        dataset.randomize(new Random());
        int trainSize = (int) Math.round(dataset.numInstances() * 0.6);
        int testSize = dataset.numInstances() - trainSize;
        Instances trainSet = new Instances(dataset, 0, trainSize);
        Instances testSet = new Instances(dataset, trainSize, testSize);

        Logistic model = (Logistic) SerializationHelper.read("src/main/resources/heart-disease-prediction.model");
        Evaluation eval = new Evaluation(testSet);
        eval.evaluateModel(model, testSet);

        System.out.println("Correctly Classified Instances: " + eval.pctCorrect() + "%");
        System.out.println("Incorrectly Classified Instances: " + eval.pctIncorrect() + "%");
        System.out.println("Precision: " + eval.precision(1));
        System.out.println("Recall: " + eval.recall(1));
        System.out.println("F1 Score: " + eval.fMeasure(1));
        System.out.println(eval.toMatrixString());

        evaluateAndPrintPredictions(model, testSet);

    }
    public static void evaluateAndPrintPredictions(Logistic model, Instances testSet) throws Exception {
        for (int i = 0; i < testSet.numInstances(); i++) {
            Instance currentInstance = testSet.instance(i);

            double actualClass = currentInstance.classValue();

            double predictedClass = model.classifyInstance(currentInstance);

            boolean isCorrect = (actualClass == predictedClass);

            System.out.println("Instance " + (i + 1) + ": " + currentInstance + " -> " + isCorrect);
        }
    }
}
