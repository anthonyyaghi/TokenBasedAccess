package simulation.queue;


import simulation.Simulation;
import simulation.events.PacketArrivalEvent;
import simulation.events.PacketDepartEvent;
import simulation.events.TimeOutEvent;

import java.util.PriorityQueue;

public class SimQueue {
    private int id;
    private PriorityQueue<SimPacket> queue;
    private Simulation sim;
    private TimeOutEvent timeOutEvent;
    private double arrClock;
    private double depClock;
    private SimPacket activePacket;
    private PacketPriority nextPacketPr;
    boolean withPriority;

    public SimQueue(int id, Simulation sim, boolean withPriority) {
        this.id = id;
        this.sim = sim;
        this.withPriority = withPriority;

        queue = new PriorityQueue<>();

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
        scheduleArrival(sim.getTime() + sim.getTimeProvider().getInterArrTime(this), pr);
    }

    public void serviceCompletion() {
        activePacket = null;

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
        long qSize = queue.stream().filter(packet -> packet.getPriority()==PacketPriority.HIGH_PRIORITY).count();
        if (activePacket != null && activePacket.getPriority() == PacketPriority.HIGH_PRIORITY) {
            qSize += 1;
        }
        return qSize;
    }

    public long countLP() {
        long qSize = queue.stream().filter(packet -> packet.getPriority()==PacketPriority.LOW_PRIORITY).count();
        if (activePacket != null && activePacket.getPriority() == PacketPriority.LOW_PRIORITY) {
            qSize += 1;
        }
        return qSize;
    }

    public double getArrClock() {
        return arrClock;
    }

    public double getDepClock() {
        return depClock;
    }

    public int getId() {
        return id;
    }

    public void scheduleArrival(double arrClock, PacketPriority pr) {
        sim.scheduleEvent(new PacketArrivalEvent(this, new SimPacket(pr),
                arrClock));
        nextPacketPr = pr;
        this.arrClock = arrClock;
    }

    public int getActivePacket() {
        return activePacket == null ? -1 : activePacket.getPriority().ordinal();
    }

    public PacketPriority getNextPacketPr() {
        return nextPacketPr;
    }

    //    ==================================================================================================================

    private void scheduleDep() {
        this.depClock = sim.getTime() + sim.getTimeProvider().getTxTime();
        activePacket = queue.poll();
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
        String desc = "{" +
                "arrClock=" + arrClock +
                " depClock=" + depClock;
        if (!withPriority)  {
            desc += " size=" + ((activePacket == null ? 0 : 1) + queue.size()) +
                    "}";
        } else {
            desc += " HPsize=" + countHP() +
                    " LPsize=" + countLP() +
                    " Packet in transmission=" + (activePacket != null ? activePacket.getPriority().toString() : "none") +
                    " Next packet=" + nextPacketPr.toString() +
                    "}";
        }

        return desc;
    }
}
