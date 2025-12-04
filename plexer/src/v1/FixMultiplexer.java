package v1;

import quickfix.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixMultiplexer {

    private final RuleBuilder ruleBuilder = new RuleBuilder();
    private final Map<String, Map<String, String>> owners = new ConcurrentHashMap<>();

    public RuleBuilder getRuleBuilder() {
        return ruleBuilder;
    }

    public RuleResult processMsg(Message msg) throws Exception {
        String msgType = msg.getHeader().getString(quickfix.field.MsgType.FIELD);
        Rule rule = ruleBuilder.getRule(msgType);

        if (rule == null)  {
            return new RuleResult(RuleType.ALL, null, "no rule, send to all sessions");
        }

        String id = rule.extractId(msg);
        owners.putIfAbsent(msgType, new ConcurrentHashMap<>());
        Map<String, String> typeOwners = owners.get(msgType);

        String sender = msg.getHeader().getString(quickfix.field.SenderCompID.FIELD);

        switch (rule.getType()) {
            case ALL:
                return new RuleResult(RuleType.ALL,  null, "send to all sessions");
            case OWNER:
                typeOwners.putIfAbsent(id, sender);
                if (sender.equals(typeOwners.get(id))) {
                    return new RuleResult(RuleType.OWNER, sender, "owner session: " + sender);
                } else {
                    return new RuleResult(RuleType.REJECT,  null, "not owner, rejected");
                }
            case SKIP:
            default:
                return new RuleResult(RuleType.SKIP, null,"skip by rule");
        }
    }
}