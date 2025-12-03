import quickfix.FieldNotFound;
import quickfix.Message;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RuleBuilder {
    private final Map<String, Rule> rules = new ConcurrentHashMap<>();

    public RuleBuilder addRule(String msgType, Function<Message, String> idExtractor, RuleType type) {
        rules.put(msgType, new Rule(msgType, idExtractor, type));
        return this;
    }

    public RuleBuilder addRule(String msgType, int tag, RuleType type) {
        rules.put(msgType, new Rule(msgType, message -> {
            try {
                return message.getString(tag);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }, type));

        return this;
    }

    public Rule getRule(String msgType) {
        return rules.get(msgType);
    }
}