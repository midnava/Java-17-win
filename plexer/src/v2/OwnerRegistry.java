package v2;

import java.util.concurrent.ConcurrentHashMap;

public class OwnerRegistry {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> owners
            = new ConcurrentHashMap<>();

    public String getOwner(String msgType, String id) {
        return owners.getOrDefault(msgType, new ConcurrentHashMap<>()).get(id);
    }

    public boolean setOwnerIfEmpty(String msgType, String id, String session) {
        owners.putIfAbsent(msgType, new ConcurrentHashMap<>());
        return owners.get(msgType).putIfAbsent(id, session) == null;
    }
}
