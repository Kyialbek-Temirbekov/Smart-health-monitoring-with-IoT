package kg.edu.manas.cloud.util;

import weka.classifiers.functions.Logistic;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

public class Test {
    public static void main(String[] args) throws Exception {

        ConverterUtils.DataSource source = new ConverterUtils.DataSource("hr-disease.arff");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(2);

        /*Logistic model = new Logistic();
        model.buildClassifier(dataset);

        SerializationHelper.write("src/main/resources/disease-prediction.model", model);*/

        //

        Logistic loadedModel = (Logistic) SerializationHelper.read("src/main/resources/disease-prediction.model");

        Instance newData = new DenseInstance(3);
        newData.setDataset(dataset);
        newData.setValue(0, 100); // ЧСС
        newData.setValue(1, 150); // Шаги

        double[] prediction = loadedModel.distributionForInstance(newData);
        System.out.println("Вероятность болезни: " + prediction[1]);

        if (prediction[1] > 0.5) {
            System.out.println("⚠️ Высокий риск заболевания!");
        } else {
            System.out.println("✅ Низкий риск заболевания.");
        }
    }
}
