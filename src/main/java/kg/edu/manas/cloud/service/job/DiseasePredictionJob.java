package kg.edu.manas.cloud.service.job;

import kg.edu.manas.cloud.model.entity.Recommendation;
import kg.edu.manas.cloud.model.repository.MetricRepository;
import kg.edu.manas.cloud.model.repository.RecommendationRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiseasePredictionJob {
    private final MetricRepository metricRepository;
    private final RecommendationRepository recommendationRepository;

    @Scheduled(cron = "0 6 16 * * *")
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
                var recommendation = Recommendation.builder()
                        .value("⚠️ High risk of heart disease!")
                        .deviceId(instance.deviceId())
                        .timestamp(LocalDateTime.now())
                        .build();
                if(!recommendationRepository.existsByDeviceIdAndTimestampIsAfter(instance.deviceId(), LocalDate.now().atStartOfDay())) {
                    recommendationRepository.save(recommendation);
                }
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
