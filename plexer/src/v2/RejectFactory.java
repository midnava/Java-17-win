package v2;

import quickfix.Message;

public interface RejectFactory {
    Message createReject(Message original, String reason);
}
