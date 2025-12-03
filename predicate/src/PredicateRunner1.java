import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class PredicateRunner1<T1> {

    public static class Rule<T1> {
        final String name;
        final int index;
        final Predicate<T1> predicate;
        final Runnable action;

        Rule(String name, int index, Predicate<T1> predicate, Runnable action) {
            this.name = name;
            this.index = index;
            this.predicate = predicate;
            this.action = action;
        }
    }

    protected final List<Rule<T1>> rules;
    protected final Map<Integer, String> indexToName;

    private PredicateRunner1(List<Rule<T1>> rules) {
        this.rules = rules;
        this.indexToName = rules.stream().collect(Collectors.toMap(r -> r.index, r -> r.name));
    }

    public int run(T1 a) {
        for (Rule<T1> r : rules) {
            if (r.predicate.test(a)) {
                r.action.run();
                return r.index;
            }
        }
        return -1;
    }

    public String getRuleNameByIndex(int index) {
        return indexToName.get(index);
    }

    public static class Builder<T1> {
        protected final List<Rule<T1>> rules = new ArrayList<>();
        protected String currentName;
        protected Predicate<T1> currentPredicate;
        protected int index = 0;
        protected boolean waitingForWhen = false;
        protected boolean waitingForThen = false;

        public Builder<T1> start(String name) {
            if (waitingForThen) throw new IllegalStateException("Must call then() before starting new rule");
            currentName = name;
            waitingForWhen = true;
            return this;
        }

        public Builder<T1> when(Predicate<T1> predicate) {
            if (!waitingForWhen) throw new IllegalStateException("start() must be called before when()");
            currentPredicate = predicate;
            waitingForWhen = false;
            waitingForThen = true;
            return this;
        }

        public Builder<T1> and(Predicate<T1> other) {
            currentPredicate = currentPredicate.and(other);
            return this;
        }

        public Builder<T1> or(Predicate<T1> other) {
            currentPredicate = currentPredicate.or(other);
            return this;
        }

        public Builder<T1> then(Runnable action) {
            if (!waitingForThen) throw new IllegalStateException("when() must be followed by then()");
            rules.add(new Rule<>(currentName, index++, currentPredicate, action));
            currentPredicate = null;
            waitingForThen = false;
            return this;
        }

        public PredicateRunner1<T1> build() {
            if (waitingForThen) throw new IllegalStateException("Last rule missing then()");
            return new PredicateRunner1<>(rules);
        }
    }
}
