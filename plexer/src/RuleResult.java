public class RuleResult {
    private final RuleType type;
    private final String owner;
    private final String info;

    public RuleResult(RuleType type, String owner, String info) {
        this.type = type;
        this.owner = owner;
        this.info = info;
    }

    @Override
    public String toString() {
        return type + "[" + owner + "]" + " (" + info + ")";
    }
}