package kg.edu.manas.cloud.util;

import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

public class ModelTest {
    public static void main(String[] args) throws Exception {

        ConverterUtils.DataSource source = new ConverterUtils.DataSource("heart-rate-disease.arff");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(2);

        /*Logistic model = new Logistic();
        model.buildClassifier(dataset);

        SerializationHelper.write("src/main/resources/disease-prediction.model", model);*/

        //

        RandomForest loadedModel = (RandomForest) SerializationHelper.read("src/main/resources/hr-disease-prediction.model");

        Instance newData = new DenseInstance(3);
        newData.setDataset(dataset);
        newData.setValue(0, 101); // ЧСС
        newData.setValue(1, 15000); // Шаги

        double[] prediction = loadedModel.distributionForInstance(newData);
        System.out.println("Probability of disease: " + prediction[1]);

        if (prediction[1] > 0.5) {
            System.out.println("⚠️ High risk of disease!");
        } else {
            System.out.println("✅ Low risk of disease.");
        }
    }
}
