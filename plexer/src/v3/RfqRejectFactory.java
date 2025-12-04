package v3;

import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.QuoteID;
import quickfix.field.RefMsgType;
import quickfix.field.Text;

public class RfqRejectFactory implements RejectFactory {

    @Override
    public Message createReject(Message msg, OutRule rule) {
        try {
            Message reject = new Message();
            reject.getHeader().setString(MsgType.FIELD, "AJ");

            String refMsgType = msg.getHeader().getString(MsgType.FIELD);
            reject.setString(RefMsgType.FIELD, refMsgType);

            if (msg.isSetField(QuoteID.FIELD)) {
                reject.setString(QuoteID.FIELD, msg.getString(QuoteID.FIELD));
            }

            reject.setString(Text.FIELD, "rejected");

            return reject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
