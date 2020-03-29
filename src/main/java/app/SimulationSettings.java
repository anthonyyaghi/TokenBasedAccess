package app;

public class SimulationSettings {
    private double replayDur;
    private int nbQ;
    private int tokenHolder;
    private boolean randomInit;
    private double[] initArr;
    private boolean genCsv;
    private boolean simpleTime;
    private double interArrTime;
    private double txTime;
    private double shortTxTime;
    private double longTxTime;
    private double timeOutTime;
    private double switchOverTime;

    public SimulationSettings() {
    }

    public double getReplayDur() {
        return replayDur;
    }

    public void setReplayDur(double replayDur) {
        this.replayDur = replayDur;
    }

    public int getNbQ() {
        return nbQ;
    }

    public void setNbQ(int nbQ) {
        this.nbQ = nbQ;
    }

    public int getTokenHolder() {
        return tokenHolder;
    }

    public void setTokenHolder(int tokenHolder) {
        this.tokenHolder = tokenHolder;
    }

    public boolean isRandomInit() {
        return randomInit;
    }

    public void setRandomInit(boolean randomInit) {
        this.randomInit = randomInit;
    }

    public double[] getInitArr() {
        return initArr;
    }

    public void setInitArr(double[] initArr) {
        this.initArr = initArr;
    }

    public boolean isGenCsv() {
        return genCsv;
    }

    public void setGenCsv(boolean genCsv) {
        this.genCsv = genCsv;
    }

    public boolean isSimpleTime() {
        return simpleTime;
    }

    public void setSimpleTime(boolean simpleTime) {
        this.simpleTime = simpleTime;
    }

    public double getInterArrTime() {
        return interArrTime;
    }

    public void setInterArrTime(double interArrTime) {
        this.interArrTime = interArrTime;
    }

    public double getTxTime() {
        return txTime;
    }

    public void setTxTime(double txTime) {
        this.txTime = txTime;
    }

    public double getTimeOutTime() {
        return timeOutTime;
    }

    public void setTimeOutTime(double timeOutTime) {
        this.timeOutTime = timeOutTime;
    }

    public double getSwitchOverTime() {
        return switchOverTime;
    }

    public void setSwitchOverTime(double switchOverTime) {
        this.switchOverTime = switchOverTime;
    }

    public double getShortTxTime() {
        return shortTxTime;
    }

    public void setShortTxTime(double shortTxTime) {
        this.shortTxTime = shortTxTime;
    }

    public double getLongTxTime() {
        return longTxTime;
    }

    public void setLongTxTime(double longTxTime) {
        this.longTxTime = longTxTime;
    }
}
