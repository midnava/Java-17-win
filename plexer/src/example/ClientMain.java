package example;

import quickfix.*;
import quickfix.field.MsgType;
import quickfix.field.QuoteID;

public class ClientMain implements Application {

    private SessionID sessionID;
    private final String clientName;

    public ClientMain(String name) {
        this.clientName = name;
    }

    public static void main(String[] args) throws Exception {
        ClientMain clientA = new ClientMain("ClientA");
        ClientMain clientB = new ClientMain("ClientB");

        SessionSettings settingsA = new SessionSettings("C:\\Users\\midna\\IdeaProjects\\Java-17-win\\plexer\\src\\example\\clientA.cfg");
        SessionSettings settingsB = new SessionSettings("C:\\Users\\midna\\IdeaProjects\\Java-17-win\\plexer\\src\\example\\clientB.cfg");

        startClient(clientA, settingsA);
        startClient(clientB, settingsB);
    }

    private static void startClient(ClientMain app, SessionSettings settings) throws Exception {
        MessageStoreFactory storeFactory = new MemoryStoreFactory();
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        SocketInitiator initiator = new SocketInitiator(
                app, storeFactory, settings, logFactory, messageFactory
        );
        initiator.start();
        Thread.sleep(2000); // ждем логон
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) {
        System.out.println(clientName + " received: " + message);
        try {
            String msgType = message.getHeader().getString(MsgType.FIELD);

            if ("R".equals(msgType)) { // RFQ
                // 1️⃣ Отправка AI (Answer/Accept Indicator)
                Message ai = new Message();
                ai.getHeader().setString(MsgType.FIELD, "AI"); // AI message
                ai.setString(QuoteID.FIELD, message.getString(131));
                Session.sendToTarget(ai, sessionID);
                System.out.println(clientName + " sent AI: " + ai);

                // 2️⃣ Через 3 секунды отправляем Quote
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        Message quote = new Message();
                        quote.getHeader().setString(MsgType.FIELD, "S"); // Quote
                        quote.setString(QuoteID.FIELD, message.getString(131));
                        Session.sendToTarget(quote, sessionID);
                        System.out.println(clientName + " sent Quote: " + quote);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) {
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
