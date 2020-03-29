package simulation.queue;

public class SimPacket implements Comparable<SimPacket> {
    private PacketPriority priority;

    public SimPacket(PacketPriority priority) {
        this.priority = priority;
    }

    public PacketPriority getPriority() {
        return priority;
    }

    @Override
    public int compareTo(SimPacket simPacket) {
        return priority.ordinal() - simPacket.priority.ordinal();
    }
}
