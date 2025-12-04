package v3;

import quickfix.Message;

public interface RejectFactory {
    Message createReject(Message original, OutRule rule);
}
