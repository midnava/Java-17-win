package v3;

import quickfix.Message;

public class BusinessRejectFactory implements RejectFactory {

    @Override
    public Message createReject(Message original, OutRule rule) {
        Message r = new Message();
        r.getHeader().setString(35, "j"); // BusinessMessageReject
        r.setString(58, "Rejected by rule: " + rule.getName());

        return r;
    }
}
