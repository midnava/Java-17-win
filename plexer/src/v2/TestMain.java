package v2;

import quickfix.*;
import quickfix.field.*;

import java.util.List;

public class TestMain {

    public static void main(String[] args) throws Exception {

        RejectFactory rejectFactory = (msg, reason) -> {
            Message r = new Message();
            r.getHeader().setString(35, "j");
            r.setString(58, reason);
            return r;
        };

        MultiplexerConfig config = new MultiplexerConfig.Builder()
                .addInRule(new InRule(
                        "R",                     // RFQ from market
                        RoutingDecision.ALL,     // send to all
                        m -> safeTag(m, 100),    // id extractor
                        "q"                      // response type = quote
                ))
                .addOutRule(new OutRule(
                        "q",                     // client quote
                        RoutingDecision.ANY,     // first wins
                        m -> safeTag(m, 100),
                        rejectFactory
                ))
                .build();

        FixMultiplexer mux = new FixMultiplexer(config);

        List<String> clients = List.of("C1", "C2");

        Message rfq = new Message();
        rfq.getHeader().setString(35, "R");
        rfq.setString(100, "123");

        System.out.println("Inbound RFQ:");
        mux.handleInbound(rfq, clients).forEach(System.out::println);

        Message q1 = new Message();
        q1.getHeader().setString(35, "q");
        q1.setString(100, "123");

        Message q2 = new Message();
        q2.getHeader().setString(35, "q");
        q2.setString(100, "123");

        System.out.println("\nOutbound C1:");
        System.out.println(mux.handleOutbound(q1, "C1"));

        System.out.println("\nOutbound C2:");
        System.out.println(mux.handleOutbound(q2, "C2"));
    }

    private static String safeTag(Message m, int tag) {
        try {
            return m.getString(tag);
        } catch (FieldNotFound e) {
            return null;
        }
    }
}
