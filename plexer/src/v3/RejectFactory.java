package v3;

import quickfix.Message;

public interface RejectFactory {
    Message createReject(Message msg, OutRule rule);
}
