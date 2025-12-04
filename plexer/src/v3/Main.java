package v3;

import quickfix.Message;

public class Main {

    public static void main(String[] args) throws Exception {

        MultiplexerConfig config = new MultiplexerConfig.Builder()
                .addInRule(new InRule("RFQ_IN", "R", RoutingDecision.ALL, m -> safe(m, 131), "S"))
                .addOutRule(new OutRule("RFQ_RESP_OUT", "S", RoutingDecision.ANY, m -> safe(m, 131), new BusinessRejectFactory()))
                .build();

        FixMultiplexer mux = new FixMultiplexer(config);

        Message rfq = new Message();
        rfq.getHeader().setString(35, "R");
        rfq.setString(131, "REQ1");

        System.out.println("IN RFQ decision: " + mux.processIncoming(rfq));

        Message resp1 = new Message();
        resp1.getHeader().setString(35, "S");
        resp1.setString(131, "REQ1");

        Message resp2 = new Message();
        resp2.getHeader().setString(35, "S");
        resp2.setString(131, "REQ1");

        System.out.println(mux.processOutgoing(resp1, "CLIENT_A"));
        System.out.println(mux.processOutgoing(resp2, "CLIENT_B"));
    }

    private static String safe(Message m, int t) {
        try {
            return m.getString(t);
        } catch (Exception e) {
            return null;
        }
    }
}
