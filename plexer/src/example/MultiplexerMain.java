package example;

import quickfix.*;

public class MultiplexerMain {

    public static void main(String[] args) throws Exception {
        SessionSettings settings = new SessionSettings("C:\\Users\\midna\\IdeaProjects\\Java-17-win\\plexer\\src\\example\\multiplexer.cfg");

        FixMultiplexerApplication app = new FixMultiplexerApplication("MARKET");

        MessageStoreFactory storeFactory = new MemoryStoreFactory();
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        SocketAcceptor acceptor = new SocketAcceptor(
                app, storeFactory, settings, logFactory, messageFactory
        );

        acceptor.start();
        System.out.println("Multiplexer started.");
    }
}
