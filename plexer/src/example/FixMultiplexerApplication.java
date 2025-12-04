package example;

import quickfix.*;
import v3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FixMultiplexerApplication implements Application {
    private final FixMultiplexer mux;
    private final String marketSenderId;
    private final List<SessionID> clientSessions = new ArrayList<>();

    public FixMultiplexerApplication(String marketSenderId) {
        // Конфигурация IN/OUT правил
        MultiplexerConfig config = new MultiplexerConfig.Builder()
                // IN rule: RFQ
                .addInRule(new InRule(
                        "RFQ_IN",
                        "R",
                        RoutingDecision.ALL,
                        m -> safe(m, 131),
                        "AI")) // response type
                // OUT rule: QuoteResponse
                .addOutRule(new OutRule(
                        "QUOTE_RESP_OUT",
                        "AI",
                        RoutingDecision.OWNER,
                        m -> safe(m, 117),
                        new BusinessRejectFactory()))
                // OUT rule: Quote with OWNER
                .addOutRule(new OutRule(
                        "QUOTE_OWNER_OUT",
                        "S",
                        RoutingDecision.OWNER,
                        m -> safe(m, 117),
                        new BusinessRejectFactory()))
                .build();

        this.mux = new FixMultiplexer(config);

        this.marketSenderId = marketSenderId;
    }

    private static String safe(Message msg, int tag) {
        try {
            return msg.getString(tag);
        } catch (FieldNotFound e) {
            return null;
        }
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) {
        try {
            //market incoming
            if (sessionID.equals(mux.getMarketSession())) {
                System.out.println("Received IN: " + message + " from " + sessionID);
                IncomingRoutingResult result = mux.processIncoming(message);
                System.out.println("IN rule result: " + result);

                RoutingDecision decision = result.getDecision();
                if (decision == RoutingDecision.ALL) {
                    for (SessionID client : clientSessions) {
                        Session.sendToTarget(message, client);
                    }
                } else if (decision == RoutingDecision.OWNER) {
                    Optional<SessionID> session = clientSessions.stream()
                            .filter(s -> s.getSenderCompID().equals(result.getOwner()))
                            .findFirst();
                    if (session.isPresent()) {
                        Session.sendToTarget(message, session.get());
                    }
                }
            } else { //client incoming msg
                RoutingResult result = mux.processOutgoing(message, sessionID.getSenderCompID());
                System.out.println("Out rule result: " + result);

                if (!result.isRejected()) {
                    mux.forwardToMarket(message);
                } else {
                    Optional<SessionID> session = clientSessions.stream()
                            .filter(s -> s.getSenderCompID().equals(result.getTargetSession()))
                            .findFirst();
                    Session.sendToTarget(message, session.get());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SessionID sessionID) {
    }

    @Override
    public void onLogon(SessionID sessionID) {
        if (sessionID.getTargetCompID().equals(marketSenderId)) {
            mux.setMarketSession(sessionID);
        } else {
            clientSessions.add(sessionID);
        }
    }

    @Override
    public void onLogout(SessionID sessionID) {
        if (sessionID.getTargetCompID().equals(marketSenderId)) {
            mux.setMarketSession(null);
        } else {
            clientSessions.removeIf(s -> s.getSenderCompID().equals(sessionID.getSenderCompID()));
        }
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void toApp(Message message, SessionID sessionID) {
    }
}
