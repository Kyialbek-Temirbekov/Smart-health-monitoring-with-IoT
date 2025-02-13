package kg.edu.manas.cloud.service.job;

import kg.edu.manas.cloud.model.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiseasePredictionJob {
    private final MetricRepository metricRepository;

    @Scheduled(cron = "0 0 10 * * *")
    public void predictHeartDisease() throws Exception {
        RandomForest model = (RandomForest) SerializationHelper.read("src/main/resources/heart-disease-prediction.model");
        Instance newData = getInstance();

        var data = metricRepository.getAvgHrStepCounts();

        data.forEach(instance -> {
            newData.setValue(0, instance.heartRate());
            newData.setValue(1, instance.stepCount());
            double[] prediction;

            try {
                prediction = model.distributionForInstance(newData);
            } catch (Exception e) {
                throw new RuntimeException("Error heart disease prediction", e);
            }

            if (prediction[1] > 0.5) {
                log.debug("⚠️ High risk of disease!");
                var deviceId = instance.deviceId();
                // create recommendation for this user
            } else {
                log.debug("✅ Low risk of disease.");
            }
        });
    }
    private Instance getInstance() throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("heart-disease.arff");
        Instances dataset = source.getDataSet();
        dataset.setClassIndex(2);
        Instance newData = new DenseInstance(3);
        newData.setDataset(dataset);
        return newData;
    }
}
