import java.util.*;
import java.util.function.Predicate;

public class PredicateRunner1<T> {

    public static class Rule<T> {
        String name;
        Predicate<T> predicate;

        Rule(String name, Predicate<T> predicate) {
            this.name = name;
            this.predicate = predicate;
        }
    }

    private final List<Rule<T>> rules;

    public PredicateRunner1(List<Rule<T>> rules) {
        this.rules = rules;
    }

    /** Возвращает индекс правила которое прошло (или -1 если ничего не прошло) */
    public int run(T value) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).predicate.test(value)) {
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

    public static class Builder<T> {

        private final List<Rule<T>> rules = new ArrayList<>();

        // building one rule
        private String currentName;
        private Predicate<T> currentPredicate;
        private boolean isOrMode = false;
        private boolean ruleStarted = false;

        public Builder<T> start(String name) {
            if (ruleStarted) {
                throw new IllegalStateException("Previous rule not finished, call then()");
            }
            this.currentName = name;
            this.currentPredicate = null;
            this.ruleStarted = true;
            this.isOrMode = false;
            return this;
        }

        private void validateRule() {
            if (!ruleStarted)
                throw new IllegalStateException("Call start() before adding predicates");
            if (currentPredicate == null)
                throw new IllegalStateException("Rule '" + currentName + "' has no predicates");
        }

        public Builder<T> when(Predicate<T> p) {
            if (!ruleStarted)
                throw new IllegalStateException("Call start() first");

            if (currentPredicate == null) {
                currentPredicate = p;
            } else {
                currentPredicate = isOrMode
                        ? currentPredicate.or(p)
                        : currentPredicate.and(p);
            }
            return this;
        }

        public Builder<T> and() {
            isOrMode = false;
            return this;
        }

        public Builder<T> or() {
            isOrMode = true;
            return this;
        }

        public Builder<T> then() {
            validateRule();
            rules.add(new Rule<>(currentName, currentPredicate));
            ruleStarted = false;
            return this;
        }

        public PredicateRunner1<T> build() {
            if (ruleStarted)
                throw new IllegalStateException("Last rule not finished, call then()");
            return new PredicateRunner1<>(rules);
        }
    }
}
