package simulation;

import simulation.events.Event;
import simulation.events.EventPriority;
import simulation.events.TimeOutEvent;
import simulation.events.TokenArrivalEvent;
import simulation.memory.Memory;
import simulation.queue.PacketPriority;
import simulation.queue.SimQueue;
import simulation.queue.Token;
import simulation.util.TimeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Simulation {
    private TimeProvider timeProvider;
    private double time;
    private PriorityQueue<Event> eventQueue;
    private SimQueue[] queues;
    private Token token;
    private int tokenHolder;

    public Simulation(int nbQueues, int tokenHolder, double[] firstArrival, TimeProvider timeProvider, boolean withPriority) {
        this.timeProvider = timeProvider;
        init(tokenHolder, nbQueues, withPriority);
        startSim(firstArrival);
    }

    public Simulation(int nbQueues, int tokenHolder, TimeProvider timeProvider, boolean withPriority) {
        this.timeProvider = timeProvider;
        init(tokenHolder, nbQueues, withPriority);

        double[] firstArrival = new double[nbQueues];
        for (int i = 0; i < nbQueues; i++) {
            firstArrival[i] = Math.random() * timeProvider.getInterArrTime(queues[i]);
        }

        startSim(firstArrival);
    }

    private void init(int tokenHolder, int nbQueues, boolean withPriority) {
        time = 0;
        this.eventQueue = new PriorityQueue<>();
        token = new Token();
        this.tokenHolder = tokenHolder;
        queues = new SimQueue[nbQueues];

        for (int i = 0; i < queues.length; i++) {
            queues[i] = new SimQueue(i, this, withPriority);
        }
    }

    private void startSim(double[] firstArrival) {
        for (int i = 0; i < queues.length; i++) {
            queues[i].scheduleArrival(firstArrival[i], PacketPriority.HIGH_PRIORITY);
        }
        scheduleEvent(new TokenArrivalEvent(queues[tokenHolder], token, 0));
    }

    public void scheduleEvent(Event event) {
        eventQueue.add(event);
    }

    public Memory simulate() {
        if (!eventQueue.isEmpty()) {
            time = eventQueue.peek().getClock();

            List<Event> events = new ArrayList<>();
            while (eventQueue.peek().getClock() == time) {
                events.add(eventQueue.poll());
            }
            for (Event event : events) {
                event.executeEvent();
            }

            double[][] queueState = new double[queues.length][6];
            for (int i = 0; i < queues.length; i++) {
                queueState[i][Memory.HIGH_PRIORITY_INDEX] = queues[i].countHP();
                queueState[i][Memory.LOW_PRIORITY_INDEX] = queues[i].countLP();
                queueState[i][Memory.ARR_CLK_INDEX] = queues[i].getArrClock();
                queueState[i][Memory.DEP_CLK_INDEX] = queues[i].getDepClock();
                queueState[i][Memory.ACTIVE_PACKET] = queues[i].getActivePacket();
                queueState[i][Memory.NEXT_PACKET] = queues[i].getNextPacketPr().ordinal();
            }
            Memory memory = new Memory(queueState, tokenHolder, timeOut(), getTOClk(), getNAClk(),time);
            System.out.println("time: " + time);
            printSimulationInfo(memory.getTimeOutClk(), memory.getNextArrClk());
            return memory;
        }
        return null;
    }

    public void passToken() {
        token.setTimeOut(false);
        scheduleEvent(new TokenArrivalEvent(queues[(tokenHolder + 1) % queues.length], token, time + timeProvider.getSwitchOverTime()));
        tokenHolder = -1;
    }

    public void updateTokenHolder(SimQueue queue) {
        for (int i = 0; i < queues.length; i++) {
            if (queues[i] == queue) {
                tokenHolder = i;
                return;
            }
        }
    }

    public boolean timeOut() {
        return token.isTimeOut();
    }

    public double getTime() {
        return time;
    }

    public void removeEvent(TimeOutEvent timeOutEvent) {
        if (timeOutEvent != null) {
            eventQueue.remove(timeOutEvent);
        }
    }

    public TimeProvider getTimeProvider() {
        return timeProvider;
    }

    private double getTOClk() {
        for (Event event : eventQueue) {
            if (event.getPriority() == EventPriority.TIME_OUT) {
                return event.getClock();
            }
        }
        return -1;
    }

    private double getNAClk() {
        for (Event event : eventQueue) {
            if (event.getPriority() == EventPriority.TOKEN_ARRIVAL) {
                return event.getClock();
            }
        }
        return -1;
    }

    private void printSimulationInfo(double timeOutClk, double nextArrClk) {
        for (SimQueue queue : queues) {
            System.out.print(queue.toString());
        }
        System.out.println("{TokenHolder=" + tokenHolder + " TimeOut=" + timeOutClk + " NextArr=" + nextArrClk + "}");
        System.out.println();
    }
}
