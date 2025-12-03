import java.util.List;

public class MyStringRunner extends PredicateRunner1<String> {

    private MyStringRunner(List<Rule<String>> rules, Runnable otherwiseAction) {
        super(rules, otherwiseAction);
    }

    public static class Builder extends PredicateRunner1.Builder<String> {

        // Helper: equals to a specific string
        public Builder equalsTo(String value) {
            this.when(x -> x.equals(value));
            return this;
        }

        // Helper: contains substring
        public Builder contains(String substring) {
            this.when(x -> x.contains(substring));
            return this;
        }

        // Helper: starts with substring
        public Builder startsWith(String prefix) {
            this.when(x -> x.startsWith(prefix));
            return this;
        }

        @Override
        public MyStringRunner build() {
            return new MyStringRunner(super.rules, super.otherwiseAction);
        }
    }
}
