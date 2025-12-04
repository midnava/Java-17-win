package v2;

import quickfix.Message;

public class RoutingResult {
    public final String target;
    public final Message message;
    public final boolean rejected;

    public RoutingResult(String target, Message message, boolean rejected) {
        this.target = target;
        this.message = message;
        this.rejected = rejected;
    }

    @Override
    public String toString() {
        return "RoutingResult{ target=" + target +
                ", msg=" + (message != null ? message.toString() : "null") +
                ", rejected=" + rejected + " }";
    }
}
