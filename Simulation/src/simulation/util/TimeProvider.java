package simulation.util;

public interface TimeProvider {
    double getInterArrTime();

    double getTxTime();

    double getTimeOut();

    double getSwitchOverTime();
}
