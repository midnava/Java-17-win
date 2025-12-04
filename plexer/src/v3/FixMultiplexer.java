package v3;

import quickfix.Message;
import quickfix.field.MsgType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixMultiplexer {

    private final MultiplexerConfig config;

    private final Map<String, Map<String, String>> owners = new ConcurrentHashMap<>();

    public FixMultiplexer(MultiplexerConfig config) {
        this.config = config;
    }

    private static String safeTag(Message msg, int tag) {
        try {
            return msg.getString(tag);
        } catch (Exception e) {
            return null;
        }
    }

    public RoutingDecision processIncoming(Message msg) throws Exception {
        String msgType = msg.getHeader().getString(MsgType.FIELD);
        InRule rule = config.getInRule(msgType);

        if (rule == null) {
            return RoutingDecision.IGNORE;
        }

        return rule.getDecision();
    }

    public RoutingResult processOutgoing(Message msg, String clientSession) throws Exception {
        String msgType = msg.getHeader().getString(MsgType.FIELD);
        OutRule rule = config.getOutRule(msgType);
        if (rule == null) return new RoutingResult(null, msg, false);

        String id = rule.getIdExtractor().apply(msg);
        owners.putIfAbsent(msgType, new ConcurrentHashMap<>());
        Map<String, String> map = owners.get(msgType);

        switch (rule.getDecision()) {
            case ANY:
                if (!map.containsKey(id)) {
                    map.put(id, clientSession);

                    return new RoutingResult("MARKET", msg, false);
                } else if (!map.get(id).equals(clientSession)) {
                    Message reject = rule.getRejectFactory().createReject(msg, rule);

                    return new RoutingResult(clientSession, reject, true);
                }

                return new RoutingResult("MARKET", msg, false);
            case OWNER:
                if (!map.containsKey(id)) {
                    map.put(id, clientSession);
                }
                if (map.get(id).equals(clientSession)) {
                    return new RoutingResult("MARKET", msg, false);
                }
                Message rejectOwner = rule.getRejectFactory().createReject(msg, rule);

                return new RoutingResult(clientSession, rejectOwner, true);
            case ALL:
                return new RoutingResult("MARKET", msg, false);
            case REJECT:
                return new RoutingResult(clientSession,
                        rule.getRejectFactory().createReject(msg, rule),
                        true);
            case IGNORE:
            default:
                return new RoutingResult(null, msg, false);
        }
    }
}
