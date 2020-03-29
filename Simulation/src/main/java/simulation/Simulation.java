package simulation;

import simulation.events.Event;
import simulation.events.TimeOutEvent;
import simulation.events.TokenArrivalEvent;
import simulation.memory.Memory;
import simulation.queue.SimQueue;
import simulation.queue.Token;
import simulation.util.TimeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.DoubleStream;

public class Simulation {
    private TimeProvider timeProvider;
    private double time;
    private PriorityQueue<Event> eventQueue;
    private SimQueue[] queues;
    private Token token;
    private int tokenHolder;

    public Simulation(int nbQueues, int tokenHolder, double[] firstArrival, TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        init(nbQueues, tokenHolder, firstArrival);
    }

    public Simulation(int nbQueues, int tokenHolder, TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        double[] firstArrival = DoubleStream.generate(() -> Math.random() * timeProvider.getInterArrTime())
                .limit(nbQueues)
                .toArray();
        init(nbQueues, tokenHolder, firstArrival);
    }

    private void init(int nbQueues, int tokenHolder, double[] firstArrival) {
        time = 0;
        this.eventQueue = new PriorityQueue<>();
        token = new Token();
        this.tokenHolder = tokenHolder;
        queues = new SimQueue[nbQueues];

        for (int i = 0; i < queues.length; i++) {
            queues[i] = new SimQueue(this, firstArrival[i]);
        }
        scheduleEvent(new TokenArrivalEvent(queues[tokenHolder], token, 0));
    }

    public void scheduleEvent(Event event) {
        eventQueue.add(event);
    }

    public Memory simulate(boolean generateMem) {
        if (!eventQueue.isEmpty()) {
            time = eventQueue.peek().getClock();

            List<Event> events = new ArrayList<>();
            while (eventQueue.peek().getClock() == time) {
                events.add(eventQueue.poll());
            }
            for (Event event : events) {
                event.executeEvent();
            }

            System.out.println("time: " + time);
            printSimulationInfo();
        }

        if (generateMem) {
            double[][] queueState = new double[queues.length][4];
            for (int i = 0; i < queues.length; i++) {
                queueState[i][Memory.HIGH_PRIORITY_INDEX] = queues[i].countHP();
                queueState[i][Memory.LOW_PRIORITY_INDEX] = queues[i].countLP();
                queueState[i][Memory.ARR_CLK_INDEX] = queues[i].getArrClock();
                queueState[i][Memory.DEP_CLK_INDEX] = queues[i].getDepClock();
            }
            return new Memory(queueState, tokenHolder, timeOut(), time);
        } else {
            return null;
        }
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

    private void printSimulationInfo() {
        for (SimQueue queue : queues) {
            System.out.print(queue.toString());
        }
        System.out.println();
        System.out.println();
    }
}
