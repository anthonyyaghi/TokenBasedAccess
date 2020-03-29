package simulation.events;

import simulation.queue.SimPacket;
import simulation.queue.SimQueue;

public class PacketArrivalEvent extends Event {
    private SimQueue queue;
    private SimPacket packet;

    public PacketArrivalEvent(SimQueue queue, SimPacket packet, double clock) {
        super(clock);
        this.queue = queue;
        this.packet = packet;
    }

    @Override
    public void executeEvent() {
        queue.receivePacket(packet);
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.PACKET_ARRIVAL;
    }

    @Override
    public String toString() {
        return "PacketArrivalEvent{" +
                "packet=" + packet.getPriority().toString() +
                '}';
    }
}
