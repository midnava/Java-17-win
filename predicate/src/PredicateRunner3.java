import java.util.*;

public class PredicateRunner3<T1, T2, T3> {

    @FunctionalInterface
    public interface TriPredicate<T1, T2, T3> {
        boolean test(T1 a, T2 b, T3 c);
    }

    public static class Rule<T1, T2, T3> {
        String name;
        TriPredicate<T1, T2, T3> predicate;

        Rule(String name, TriPredicate<T1, T2, T3> predicate) {
            this.name = name;
            this.predicate = predicate;
        }
    }

    private final List<Rule<T1, T2, T3>> rules;

    public PredicateRunner3(List<Rule<T1, T2, T3>> rules) {
        this.rules = rules;
    }

    public int run(T1 a, T2 b, T3 c) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).predicate.test(a, b, c)) {
                return i;
            }
        }
        return -1;
    }

    public String getRuleName(int index) {
        if (index < 0 || index >= rules.size()) return null;
        return rules.get(index).name;
    }

    // =====================================================================
    //                           BUILDER
    // =====================================================================

    public static class Builder<T1, T2, T3> {

        private final List<Rule<T1, T2, T3>> rules = new ArrayList<>();

        private String currentName;
        private TriPredicate<T1, T2, T3> currentPredicate;
        private boolean isOrMode = false;
        private boolean ruleStarted = false;

        public Builder<T1, T2, T3> start(String name) {
            if (ruleStarted)
                throw new IllegalStateException("Previous rule not finished");
            currentName = name;
            currentPredicate = null;
            ruleStarted = true;
            isOrMode = false;
            return this;
        }

        private void validateRule() {
            if (!ruleStarted)
                throw new IllegalStateException("Call start() first");
            if (currentPredicate == null)
                throw new IllegalStateException("Rule '" + currentName + "' has no predicates");
        }

        public Builder<T1, T2, T3> when(TriPredicate<T1, T2, T3> p) {
            if (!ruleStarted)
                throw new IllegalStateException("Call start() first");

            if (currentPredicate == null) {
                currentPredicate = p;
            } else {
                currentPredicate = isOrMode
                        ? orWrap(currentPredicate, p)
                        : andWrap(currentPredicate, p);
            }
            return this;
        }

        private TriPredicate<T1, T2, T3> andWrap(TriPredicate<T1, T2, T3> a, TriPredicate<T1, T2, T3> b) {
            return (x, y, z) -> a.test(x, y, z) && b.test(x, y, z);
        }

        private TriPredicate<T1, T2, T3> orWrap(TriPredicate<T1, T2, T3> a, TriPredicate<T1, T2, T3> b) {
            return (x, y, z) -> a.test(x, y, z) || b.test(x, y, z);
        }

        public Builder<T1, T2, T3> and() {
            isOrMode = false;
            return this;
        }

        public Builder<T1, T2, T3> or() {
            isOrMode = true;
            return this;
        }

        public Builder<T1, T2, T3> then() {
            validateRule();
            rules.add(new Rule<>(currentName, currentPredicate));
            ruleStarted = false;
            return this;
        }

        public PredicateRunner3<T1, T2, T3> build() {
            if (ruleStarted)
                throw new IllegalStateException("Rule not finished");
            return new PredicateRunner3<>(rules);
        }
    }
}
