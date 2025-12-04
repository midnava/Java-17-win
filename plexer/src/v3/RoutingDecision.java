package v3;

public enum RoutingDecision {
    ALL,        // broadcast
    OWNER,      // only owner (capture first responder)
    ANY,        // first client wins; others rejected
    REJECT,     // always reject incoming/outgoing
    IGNORE      // skip routing, do nothing
}
