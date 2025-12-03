import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;
import quickfix.field.QuoteReqID;
import quickfix.field.SenderCompID;

import java.util.Arrays;
import java.util.List;

public class FixMultiplexerTest {

    public static void main(String[] args) throws Exception {
        FixMultiplexer multiplexer = new FixMultiplexer();
        multiplexer.getRuleBuilder()
                .addRule("R", QuoteReqID.FIELD, RuleType.ALL)   // RFQ
                .addRule("Q", ClOrdID.FIELD, RuleType.OWNER)  // Quote
                .addRule("ER", ClOrdID.FIELD, RuleType.OWNER);  // ER

        // Создаем тестовые сообщения
        Message rfq1 = new Message();
        rfq1.getHeader().setField(new MsgType("R"));
        rfq1.setField(new QuoteReqID("1"));
        rfq1.getHeader().setField(new SenderCompID("market"));

        Message rfq2 = new Message();
        rfq2.getHeader().setField(new MsgType("R"));
        rfq2.setField(new QuoteReqID("1"));
        rfq2.getHeader().setField(new SenderCompID("market"));

        Message quote1 = new Message();
        quote1.getHeader().setField(new MsgType("Q"));
        quote1.setField(new ClOrdID("100"));
        quote1.getHeader().setField(new SenderCompID("clientA"));

        Message quote2 = new Message();
        quote2.getHeader().setField(new MsgType("Q"));
        quote2.setField(new ClOrdID("100"));
        quote2.getHeader().setField(new SenderCompID("clientB"));

        List<Message> incoming = Arrays.asList(rfq1, rfq2, quote1, quote2);

        System.out.println("=== Incoming Messages ===");
        for (Message msg : incoming) {
            RuleResult result = multiplexer.processMsg(msg);
            System.out.println("Message: " + msg + " -> " + result);
        }
    }
}