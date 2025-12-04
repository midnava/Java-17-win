package v2;

import quickfix.Message;
import java.util.function.Function;

public class OutRule {
    private final String msgType;
    private final RoutingDecision decision;
    private final Function<Message,String> idExtractor;
    private final RejectFactory rejectFactory;

    public OutRule(String msgType,
                   RoutingDecision decision,
                   Function<Message,String> idExtractor,
                   RejectFactory rejectFactory) {
        this.msgType = msgType;
        this.decision = decision;
        this.idExtractor = idExtractor;
        this.rejectFactory = rejectFactory;
    }

    public String getMsgType() { return msgType; }
    public RoutingDecision getDecision() { return decision; }
    public Function<Message,String> getIdExtractor() { return idExtractor; }
    public RejectFactory getRejectFactory() { return rejectFactory; }
}
