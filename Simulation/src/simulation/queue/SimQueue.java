package simulation.queue;


import simulation.Simulation;
import simulation.events.PacketArrivalEvent;
import simulation.events.PacketDepartEvent;
import simulation.events.TimeOutEvent;

import java.util.PriorityQueue;

public class SimQueue {
    private PriorityQueue<SimPacket> queue;
    private Simulation sim;
    private TimeOutEvent timeOutEvent;
    private double arrClock;
    private double depClock;

    public SimQueue(Simulation sim, double firstArrival) {
        this.sim = sim;

        queue = new PriorityQueue<>();

        scheduleArrival(firstArrival, PacketPriority.HIGH_PRIORITY);

        depClock = -1;
    }

    public void receivePacket(SimPacket packet) {
        queue.add(packet);

        PacketPriority pr;
        if (packet.getPriority() == PacketPriority.HIGH_PRIORITY) {
            pr = PacketPriority.LOW_PRIORITY;
        } else {
            pr = PacketPriority.HIGH_PRIORITY;
        }
        scheduleArrival(sim.getTime() + sim.getTimeProvider().getInterArrTime(), pr);
    }

    public void serviceCompletion() {
        queue.poll();

        if (queue.isEmpty()) {
            passToken();
        } else {
            if (sim.timeOut()) {
                passToken();
            } else {
                scheduleDep();
            }
        }
    }

    public void tokenArrived(Token token) {
        sim.updateTokenHolder(this);
        if (queue.isEmpty()) {
            sim.passToken();
        } else {
            scheduleTimeOut(token);
            scheduleDep();
        }
    }

    public long countHP() {
        return queue.stream().filter(packet -> packet.getPriority()==PacketPriority.HIGH_PRIORITY).count();
    }

    public long countLP() {
        return queue.stream().filter(packet -> packet.getPriority()==PacketPriority.LOW_PRIORITY).count();
    }

    public double getArrClock() {
        return arrClock;
    }

    public double getDepClock() {
        return depClock;
    }

    //    ==================================================================================================================

    private void scheduleArrival(double arrClock, PacketPriority pr) {
        sim.scheduleEvent(new PacketArrivalEvent(this, new SimPacket(pr),
                arrClock));
        this.arrClock = arrClock;
    }

    private void scheduleDep() {
        this.depClock = sim.getTime() + sim.getTimeProvider().getTxTime();
        sim.scheduleEvent(new PacketDepartEvent(this, depClock));
    }

    private void passToken() {
        depClock = -1;
        sim.removeEvent(timeOutEvent);
        sim.passToken();
    }

    private void scheduleTimeOut(Token token) {
        timeOutEvent = new TimeOutEvent(token, sim.getTime() + sim.getTimeProvider().getTimeOut());
        sim.scheduleEvent(timeOutEvent);
    }

    @Override
    public String toString() {
        return "{" +
                "size=" + queue.size() +
                " arrClock=" + arrClock +
                " depClock=" + depClock +
                "}";
    }
}
