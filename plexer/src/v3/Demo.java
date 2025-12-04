package v3;

import quickfix.Message;

public class Demo {
    public static void main(String[] args) throws Exception {

        MultiplexerConfig config = new MultiplexerConfig.Builder()
                .addInRule(new InRule(
                        "QUOTE_IN",
                        "R",
                        RoutingDecision.ALL,
                        m -> safe(m, 117),
                        "S"
                ))
                .addInRule(new InRule(
                        "QUOTERESP_IN",
                        "AI",
                        RoutingDecision.IGNORE,
                        m -> safe(m, 117),
                        null
                ))
                .addOutRule(new OutRule(
                        "QUOTE_RESP_OUT",
                        "AI",
                        RoutingDecision.ANY,      // первый → owner
                        m -> safe(m, 117),
                        new BusinessRejectFactory()
                ))
                .addOutRule(new OutRule(
                        "QUOTE_OUT",
                        "S",
                        RoutingDecision.OWNER,
                        m -> safe(m, 117),
                        new BusinessRejectFactory()
                ))
                .build();


        FixMultiplexer mux = new FixMultiplexer(config);


        Message quote = new Message();
        quote.getHeader().setString(35, "R");
        quote.setString(117, "Q777");

        System.out.println("IN QUOTE decision: " + mux.processIncoming(quote));

        Message respA = new Message();
        respA.getHeader().setString(35, "AI");
        respA.setString(117, "Q777");

        System.out.println("OUT A (AI): " + mux.processOutgoing(respA, "CLIENT_A"));


        Message respB = new Message();
        respB.getHeader().setString(35, "AI");
        respB.setString(117, "Q777");

        System.out.println("OUT B (AI): " + mux.processOutgoing(respB, "CLIENT_B"));


        Message sQuote = new Message();
        sQuote.getHeader().setString(35, "S");
        sQuote.setString(117, "Q777");

        System.out.println("OUT B (S): " + mux.processOutgoing(sQuote, "CLIENT_B"));

        System.out.println("OUT A (S): " + mux.processOutgoing(sQuote, "CLIENT_A"));
    }


    private static String safe(Message m, int tag) {
        try {
            return m.getString(tag);
        } catch (Exception e) {
            return null;
        }
    }
}
