import java.util.*;

public class PredicateRunner2<T1, T2> {

    @FunctionalInterface
    public interface BiPredicate<T1, T2> {
        boolean test(T1 a, T2 b);
    }

    public static class Rule<T1, T2> {
        String name;
        BiPredicate<T1, T2> predicate;

        Rule(String name, BiPredicate<T1, T2> predicate) {
            this.name = name;
            this.predicate = predicate;
        }
    }

    private final List<Rule<T1, T2>> rules;

    public PredicateRunner2(List<Rule<T1, T2>> rules) {
        this.rules = rules;
    }

    public int run(T1 a, T2 b) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).predicate.test(a, b)) {
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

    public static class Builder<T1, T2> {

        private final List<Rule<T1, T2>> rules = new ArrayList<>();

        private String currentName;
        private BiPredicate<T1, T2> currentPredicate;
        private boolean isOrMode = false;
        private boolean ruleStarted = false;

        public Builder<T1, T2> start(String name) {
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

        public Builder<T1, T2> when(BiPredicate<T1, T2> p) {
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

        private BiPredicate<T1, T2> andWrap(BiPredicate<T1, T2> a, BiPredicate<T1, T2> b) {
            return (x, y) -> a.test(x, y) && b.test(x, y);
        }

        private BiPredicate<T1, T2> orWrap(BiPredicate<T1, T2> a, BiPredicate<T1, T2> b) {
            return (x, y) -> a.test(x, y) || b.test(x, y);
        }

        public Builder<T1, T2> and() {
            isOrMode = false;
            return this;
        }

        public Builder<T1, T2> or() {
            isOrMode = true;
            return this;
        }

        public Builder<T1, T2> then() {
            validateRule();
            rules.add(new Rule<>(currentName, currentPredicate));
            ruleStarted = false;
            return this;
        }

        public PredicateRunner2<T1, T2> build() {
            if (ruleStarted)
                throw new IllegalStateException("Rule not finished");
            return new PredicateRunner2<>(rules);
        }
    }
}
