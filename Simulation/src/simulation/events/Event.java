package simulation.events;

public abstract class Event implements Comparable<Event> {
    private double clock;

    public Event(double clock) {
        this.clock = clock;
    }

    public abstract void executeEvent();

    public abstract EventPriority getPriority();

    public double getClock() {
        return clock;
    }

    @Override
    public int compareTo(Event event) {
        if (clock == event.clock) {
            return getPriority().ordinal() - event.getPriority().ordinal();
        }
        if (clock < event.clock) {
            return -1;
        }
        return 1;
    }
}
