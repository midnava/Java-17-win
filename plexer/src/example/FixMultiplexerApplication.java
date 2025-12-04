package example;

import quickfix.Application;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import v3.*;

public class FixMultiplexerApplication implements Application {

    private final FixMultiplexer mux;

    public FixMultiplexerApplication() {
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
                        RoutingDecision.ANY,
                        m -> safe(m, 131),
                        new BusinessRejectFactory()))
                // OUT rule: Quote with OWNER
                .addOutRule(new OutRule(
                        "QUOTE_OWNER_OUT",
                        "S",
                        RoutingDecision.OWNER,
                        m -> safe(m, 131),
                        new BusinessRejectFactory()))
                .build();

        this.mux = new FixMultiplexer(config);
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
            System.out.println("Received IN: " + message + " from " + sessionID);
            RoutingDecision result = mux.processIncoming(message);

            System.out.println("IN rule result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SessionID sessionID) {
    }

    @Override
    public void onLogon(SessionID sessionID) {
        System.out.println("Session logon: " + sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        System.out.println("Session logout: " + sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void toApp(Message message, SessionID sessionID) {
    }
}
