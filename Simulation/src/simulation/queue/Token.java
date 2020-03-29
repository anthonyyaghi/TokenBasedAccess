package simulation.queue;

public class Token {
    boolean timeOut;

    public Token() {
        timeOut = false;
    }

    public boolean isTimeOut() {
        return timeOut;
    }

    public void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
    }
}
