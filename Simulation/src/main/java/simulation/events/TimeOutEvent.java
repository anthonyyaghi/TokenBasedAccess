package simulation.events;

import simulation.queue.Token;

public class TimeOutEvent extends Event {
    private Token token;

    public TimeOutEvent(Token token, double clock) {
        super(clock);
        this.token = token;
    }

    @Override
    public void executeEvent() {
        token.setTimeOut(true);
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.TIME_OUT;
    }

    @Override
    public String toString() {
        return "TimeOutEvent{}";
    }
}
