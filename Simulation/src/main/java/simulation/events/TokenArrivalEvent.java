package simulation.events;

import simulation.queue.SimQueue;
import simulation.queue.Token;

public class TokenArrivalEvent extends Event {
    private SimQueue queue;
    private Token token;

    public TokenArrivalEvent(SimQueue queue, Token token, double clock) {
        super(clock);
        this.queue = queue;
        this.token = token;
    }

    @Override
    public void executeEvent() {
        queue.tokenArrived(token);
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.TOKEN_ARRIVAL;
    }

    @Override
    public String toString() {
        return "TokenArrivalEvent{}";
    }
}
