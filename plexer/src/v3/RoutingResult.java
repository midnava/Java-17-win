package v3;

import quickfix.Message;

public class RoutingResult {
    private final String targetSession;
    private final Message message;
    private final boolean rejected;

    public RoutingResult(String targetSession, Message message, boolean rejected) {
        this.targetSession = targetSession;
        this.message = message;
        this.rejected = rejected;
    }

    public String getTargetSession() { return targetSession; }
    public Message getMessage() { return message; }
    public boolean isRejected() { return rejected; }

    @Override
    public String toString() {
        return "RoutingResult[target=" + targetSession +
                ", rejected=" + rejected +
                ", msg=" + message + "]";
    }
}
