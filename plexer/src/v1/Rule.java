package v1;

import quickfix.Message;
import java.util.function.Function;

public class Rule {
    private final String msgType;
    private final Function<Message, String> idExtractor;
    private final RuleType type;

    public Rule(String msgType, Function<Message, String> idExtractor, RuleType type) {
        this.msgType = msgType;
        this.idExtractor = idExtractor;
        this.type = type;
    }

    public String extractId(Message msg) {
        return idExtractor.apply(msg);
    }

    public RuleType getType() {
        return type;
    }
}