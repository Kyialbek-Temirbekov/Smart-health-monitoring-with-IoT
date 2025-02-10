package kg.edu.manas.cloud.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

@UtilityClass
public class StatisticsUtil {
    private final Mean mean = new Mean();
    private final StandardDeviation stdDev = new StandardDeviation();
    private final PearsonsCorrelation prsCor = new PearsonsCorrelation();

    public double findMean(double[] data) {
        return mean.evaluate(data);
    }
    public double findStandardDeviation(double[] data) {
        return stdDev.evaluate(data);
    }
    public double findPearsonsCorrelation(double[] x, double[] y) {
        return prsCor.correlation(x, y);
    }
}
