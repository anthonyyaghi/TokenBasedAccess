package simulation.util;

import simulation.queue.SimQueue;

public class StochasticTimeProvider implements TimeProvider {
    private double[] interArrTimeMean;
    private double shortTxTimeMean;
    private double longTxTimeMean;
    private double timeOut;
    private double switchOverTime;

    public StochasticTimeProvider(double[] interArrTimeMean, double shortTxTimeMean, double longTxTimeMean,
                                  double timeOut, double switchOverTime) {
        this.interArrTimeMean = interArrTimeMean;
        this.shortTxTimeMean = shortTxTimeMean;
        this.longTxTimeMean = longTxTimeMean;
        this.timeOut = timeOut;
        this.switchOverTime = switchOverTime;
    }

    @Override
    public double getInterArrTime(SimQueue queue) {
        return sampleExp(1. / interArrTimeMean[queue.getId()]);
    }

    @Override
    public double getTxTime() {
        double p = Math.random();
        if (p < 0.8) {
            return sampleExp(1. / shortTxTimeMean);
        } else {
            return sampleExp(1. / longTxTimeMean);
        }
    }

    @Override
    public double getTimeOut() {
        return timeOut;
    }

    @Override
    public double getSwitchOverTime() {
        return switchOverTime;
    }

    private double sampleExp(double a) {
        // Sampling exp(a) using inverse transformation method
        // mean = 1/a
        // Math.Random() generates random numbers between 0 and 1 with a uniform distribution
        return -Math.log(1 - Math.random()) / a;
    }
}
