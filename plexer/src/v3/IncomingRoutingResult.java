package v3;

public class IncomingRoutingResult {
    private final InRule rule;
    private final String owner; // может быть null, если ещё нет владельца
    private final RoutingDecision decision;

    public IncomingRoutingResult(InRule rule, String owner, RoutingDecision decision) {
        this.rule = rule;
        this.owner = owner;
        this.decision = decision;
    }

    public InRule getRule() {
        return rule;
    }

    public String getOwner() {
        return owner;
    }

    public RoutingDecision getDecision() {
        return decision;
    }

    @Override
    public String toString() {
        String ruleName = rule == null ? "none" : rule.getName();

        return "IncomingRoutingResult{" +
                "rule=" + ruleName +
                ", owner=" + owner +
                ", decision=" + decision +
                '}';
    }
}
