package v2;

import quickfix.FieldNotFound;
import quickfix.Message;

import java.util.*;
import java.util.stream.Collectors;

public class FixMultiplexer {

    private final MultiplexerConfig config;
    private final OwnerRegistry owners = new OwnerRegistry();

    public FixMultiplexer(MultiplexerConfig config) {
        this.config = config;
    }

    public List<RoutingResult> handleInbound(Message marketMsg, List<String> clientSessions) throws FieldNotFound {
        String msgType = marketMsg.getHeader().getString(35);
        InRule rule = config.getInRules().get(msgType);
        if (rule == null) return List.of();

        switch (rule.getDecision()) {
            case IGNORE:
                return List.of();

            case ALL:
                return clientSessions.stream()
                        .map(s -> new RoutingResult(s, marketMsg, false))
                        .collect(Collectors.toList());

            case OWNER:
                String id = rule.getIdExtractor().apply(marketMsg);
                String owner = owners.getOwner(msgType, id);
                if (owner == null) return List.of();
                return List.of(new RoutingResult(owner, marketMsg, false));

            case REJECT:
                return clientSessions.stream()
                        .map(s -> new RoutingResult(s, null, true))
                        .collect(Collectors.toList());

            default:
                return List.of();
        }
    }

    public RoutingResult handleOutbound(Message clientMsg, String clientSession) throws FieldNotFound {
        String msgType = clientMsg.getHeader().getString(35);
        OutRule rule = config.getOutRules().get(msgType);
        if (rule == null) {
            return new RoutingResult(clientSession, null, true);
        }

        String id = rule.getIdExtractor().apply(clientMsg);
        String existingOwner = owners.getOwner(msgType, id);

        switch (rule.getDecision()) {
            case ANY:
                if (existingOwner == null) {
                    owners.setOwnerIfEmpty(msgType, id, clientSession);
                    return new RoutingResult("MARKET", clientMsg, false);

                } else if (existingOwner.equals(clientSession)) {
                    return new RoutingResult("MARKET", clientMsg, false);
                } else {
                    Message reject = rule.getRejectFactory().createReject(clientMsg, "Already answered");

                    return new RoutingResult(clientSession, reject, true);
                }

            case OWNER:
                if (existingOwner == null || !existingOwner.equals(clientSession)) {
                    Message reject = rule.getRejectFactory().createReject(clientMsg, "Not owner");
                    return new RoutingResult(clientSession, reject, true);
                }
                return new RoutingResult("MARKET", clientMsg, false);

            case ALL:
                return new RoutingResult("MARKET", clientMsg, false);

            case REJECT:
                Message reject1 = rule.getRejectFactory().createReject(clientMsg, "Rule reject");
                return new RoutingResult(clientSession, reject1, true);

            case IGNORE:
                return new RoutingResult(clientSession, null, true);

            default:
                return new RoutingResult(clientSession, null, true);
        }
    }
}
