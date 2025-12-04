package v3;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixMultiplexer {

    private final MultiplexerConfig config;
    private SessionID marketSession;

    private final Map<String, String> owners = new ConcurrentHashMap<>();

    public FixMultiplexer(MultiplexerConfig config) {
        this.config = config;
    }

    private String safeTag(Message msg, int tag) {
        try {
            return msg.getString(tag);
        } catch (Exception e) {
            return null;
        }
    }

    public IncomingRoutingResult processIncoming(Message msg) throws Exception {
        String msgType = msg.getHeader().getString(MsgType.FIELD);
        InRule rule = config.getInRule(msgType);

        if (rule == null) {
            return new IncomingRoutingResult(null, null, RoutingDecision.IGNORE);
        }

        // Получаем ID из сообщения
        String id = rule.getIdExtractor().apply(msg);
        String owner = owners.get(id);

        return new IncomingRoutingResult(rule, owner, rule.getDecision());
    }

    public RoutingResult processOutgoing(Message msg, String clientSession) throws Exception {
        String msgType = msg.getHeader().getString(MsgType.FIELD);
        OutRule rule = config.getOutRule(msgType);
        if (rule == null) {
            return new RoutingResult(null, msg, false);
        }

        String id = rule.getIdExtractor().apply(msg);

        switch (rule.getDecision()) {
            case ANY:
                if (!owners.containsKey(id)) {
                    owners.put(id, clientSession);

                    return new RoutingResult("MARKET", msg, false);
                } else if (!owners.get(id).equals(clientSession)) {
                    Message reject = rule.getRejectFactory().createReject(msg, rule);

                    return new RoutingResult(clientSession, reject, true);
                }

                return new RoutingResult("MARKET", msg, false);
            case OWNER:
                if (!owners.containsKey(id)) {
                    owners.put(id, clientSession);
                }
                if (owners.get(id).equals(clientSession)) {
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

    public void forwardToMarket(Message message) {
        try {
            Message copy = (Message) message.clone();

            // Меняем заголовок
            SessionID marketSession = getMarketSession(); // метод для получения SessionID рынка
            copy.getHeader().setString(SenderCompID.FIELD, marketSession.getSenderCompID());
            copy.getHeader().setString(TargetCompID.FIELD, marketSession.getTargetCompID());

            // Отправляем
            Session.sendToTarget(copy, marketSession);
            System.out.println("Forwarded to Market: " + copy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMarketSession(SessionID market) {
        this.marketSession = market;
    }

    public SessionID getMarketSession() {
        return marketSession;
    }
}
