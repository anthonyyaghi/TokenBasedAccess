package simulation.memory;

import java.util.Arrays;

public class Memory {
    public static final int HIGH_PRIORITY_INDEX = 0;
    public static final int LOW_PRIORITY_INDEX = 1;
    public static final int ARR_CLK_INDEX = 2;
    public static final int DEP_CLK_INDEX = 3;

    // queueStat is a double array holding the number of high priority and low priority events of each queue
    private double[][] queueState;
    private int tokenHolder;
    private boolean timeOut;
    private double time;

    public Memory(double[][] queueState, int tokenHolder, boolean timeOut, double time) {
        this.queueState = queueState;
        this.tokenHolder = tokenHolder;
        this.timeOut = timeOut;
        this.time = time;
    }

    public double[][] getQueueState() {
        return queueState;
    }

    public int getTokenHolder() {
        return tokenHolder;
    }

    public boolean isTimeOut() {
        return timeOut;
    }

    public double getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Memory{" +
                "queueState=" + Arrays.toString(queueState) +
                ", tokenHolder=" + tokenHolder +
                ", timeOut=" + timeOut +
                ", time=" + time +
                '}';
    }
}
