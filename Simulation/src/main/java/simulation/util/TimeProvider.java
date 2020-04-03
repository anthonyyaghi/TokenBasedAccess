package simulation.util;

import simulation.queue.SimQueue;

public interface TimeProvider {
    double getInterArrTime(SimQueue queue);

    double getTxTime();

    double getTimeOut();

    double getSwitchOverTime();
}
