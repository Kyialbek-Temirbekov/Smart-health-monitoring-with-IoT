package kg.edu.manas.cloud.util;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DatasetGenerator {

    public static void main(String[] args) throws IOException {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("meanHR"));
        attributes.add(new Attribute("steps"));

        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("0");
        classValues.add("1");
        attributes.add(new Attribute("disease", classValues));

        Instances dataset = new Instances("HRDisease", attributes, 0);
        dataset.setClassIndex(2);

        Random rand = new Random();
        for (int i = 0; i < 20000; i++) {
            double heartRate = generateHeartRate(rand,40,120);
            int steps = generateSteps(rand, 0, 50000);
            int diseaseRisk = generateDiseaseRisk(heartRate, steps);

            addInstance(dataset, heartRate, steps, diseaseRisk);
        }
        for (int i = 0; i < 30000; i++) {
            double heartRate = generateHeartRate(rand, 90, 90);
            int steps = generateSteps(rand, 0, 5000);
            int diseaseRisk = generateDiseaseRisk(heartRate, steps);

            addInstance(dataset, heartRate, steps, diseaseRisk);
        }

        saveDatasetToFile(dataset, "src/main/resources/heart-rate-disease.arff");
        System.out.println("Dataset saved successfully.");
    }

    private static double generateHeartRate(Random rand, int min, int bound) {
        return min + rand.nextInt(bound);
    }

    private static int generateSteps(Random rand, int min, int bound) {
        return rand.nextInt(bound) + min;
    }

    private static int generateDiseaseRisk(double heartRate, int steps) {
        if (heartRate > 100 && steps < 4000) {
            return 1;
        } else {
            return 0;
        }
    }

    private static void addInstance(Instances dataset, double heartRate, int steps, int diseaseRisk) {
        DenseInstance instance = new DenseInstance(3);
        instance.setDataset(dataset);
        instance.setValue(0, heartRate);
        instance.setValue(1, steps);
        instance.setValue(2, diseaseRisk);
        dataset.add(instance);
    }

    private static void saveDatasetToFile(Instances dataset, String filename) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataset);
        saver.setFile(new File(filename));
        saver.writeBatch();
    }
}

