package example;

import quickfix.*;
import quickfix.field.MsgType;
import quickfix.field.QuoteReqID;

public class MarketMain implements Application {

    private SessionID sessionID;

    public static void main(String[] args) throws Exception {
        SessionSettings settings = new SessionSettings("C:\\Users\\midna\\IdeaProjects\\Java-17-win\\plexer\\src\\example\\market.cfg");
        MarketMain app = new MarketMain();

        MessageStoreFactory storeFactory = new MemoryStoreFactory();
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        SocketInitiator initiator = new SocketInitiator(
                app, storeFactory, settings, logFactory, messageFactory
        );
        initiator.start();

        Thread.sleep(5000);
        app.sendRFQ("REQ1");

        Thread.sleep(5000);
        app.sendRFQ("REQ2");
    }

    public void sendRFQ(String reqId) throws SessionNotFound {
        Message rfq = new Message();
        rfq.getHeader().setString(MsgType.FIELD, "R");
        rfq.setString(QuoteReqID.FIELD, reqId);
        Session.sendToTarget(rfq, sessionID);
        System.out.println("Market sent RFQ: " + rfq);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) {
    }

    @Override
    public void onCreate(SessionID sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public void onLogon(SessionID sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public void onLogout(SessionID sessionID) {
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
    }

    @Override
    public void toApp(Message message, SessionID sessionID) {
    }
}
