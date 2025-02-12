package kg.edu.manas.cloud.util;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

public class ModelBuild {
    public static void main(String[] args) throws Exception {

        ConverterUtils.DataSource source = new ConverterUtils.DataSource("heart-rate-disease.arff");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(2);

//        Logistic model = new Logistic();
        RandomForest model = new RandomForest();
        model.buildClassifier(dataset);

        SerializationHelper.write("src/main/resources/hr-disease-prediction.model", model);

        ConverterUtils.DataSource testSource = new ConverterUtils.DataSource("heart-rate-disease-test.arff");
        Instances testDataset = testSource.getDataSet();
        testDataset.setClassIndex(2);

        Evaluation eval = new Evaluation(testDataset);

        eval.evaluateModel(model, testDataset);

        // 5. Выводим основные метрики
        System.out.println("Correctly Classified Instances: " + eval.pctCorrect() + "%");
        System.out.println("Incorrectly Classified Instances: " + eval.pctIncorrect() + "%");
        System.out.println("Precision: " + eval.precision(1));
        System.out.println("Recall: " + eval.recall(1));
        System.out.println("F1 Score: " + eval.fMeasure(1));
        System.out.println(eval.toMatrixString());
    }
}
