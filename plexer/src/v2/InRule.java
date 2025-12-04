package v2;

import quickfix.Message;
import java.util.function.Function;

public class InRule {
    private final String msgType;
    private final RoutingDecision decision;
    private final Function<Message, String> idExtractor;
    private final String responseMsgType;

    public InRule(String msgType,
                  RoutingDecision decision,
                  Function<Message,String> idExtractor,
                  String responseMsgType) {
        this.msgType = msgType;
        this.decision = decision;
        this.idExtractor = idExtractor;
        this.responseMsgType = responseMsgType;
    }

    public String getMsgType() { return msgType; }
    public RoutingDecision getDecision() { return decision; }
    public Function<Message, String> getIdExtractor() { return idExtractor; }
    public String getResponseMsgType() { return responseMsgType; }
}
