package simulation.util;

public class SimpleTimeProvider implements TimeProvider {
    private double interArrTime;
    private double txTime;
    private double timeOut;
    private double switchOverTime;

    public SimpleTimeProvider(double interArrTime, double txTime, double timeOut, double switchOverTime) {
        this.interArrTime = interArrTime;
        this.txTime = txTime;
        this.timeOut = timeOut;
        this.switchOverTime = switchOverTime;
    }

    @Override
    public double getInterArrTime() {
        return interArrTime;
    }

    @Override
    public double getTxTime() {
        return txTime;
    }

    @Override
    public double getTimeOut() {
        return timeOut;
    }

    @Override
    public double getSwitchOverTime() {
        return switchOverTime;
    }
}
