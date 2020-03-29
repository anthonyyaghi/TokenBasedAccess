package simulation.events;

import simulation.queue.SimQueue;

public class PacketDepartEvent extends Event {
    private SimQueue queue;

    public PacketDepartEvent(SimQueue queue, double clock) {
        super(clock);
        this.queue = queue;
    }

    @Override
    public void executeEvent() {
        queue.serviceCompletion();
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.PACKET_DEPARTURE;
    }

    @Override
    public String toString() {
        return "PacketDepartEvent{}";
    }
}
