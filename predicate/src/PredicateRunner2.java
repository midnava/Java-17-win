import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class PredicateRunner2<T1, T2> {

    public static class Rule<T1, T2> {
        final String name;
        final int index;
        final BiPredicate<T1, T2> predicate;
        final Runnable action;

        Rule(String name, int index, BiPredicate<T1, T2> predicate, Runnable action) {
            this.name = name;
            this.index = index;
            this.predicate = predicate;
            this.action = action;
        }
    }

    private final List<Rule<T1, T2>> rules;
    private final Map<Integer, String> indexToName;

    private PredicateRunner2(List<Rule<T1, T2>> rules) {
        this.rules = rules;
        this.indexToName = rules.stream().collect(Collectors.toMap(r -> r.index, r -> r.name));
    }

    public int run(T1 a, T2 b) {
        for (Rule<T1, T2> r : rules) {
            if (r.predicate.test(a, b)) {
                r.action.run();
                return r.index;
            }
        }
        return -1;
    }

    public String getRuleNameByIndex(int index) {
        return indexToName.get(index);
    }

    public static class Builder<T1, T2> {
        private final List<Rule<T1, T2>> rules = new ArrayList<>();
        private String currentName;
        private BiPredicate<T1, T2> currentPredicate;
        private int index = 0;
        private boolean waitingForWhen = false;
        private boolean waitingForThen = false;

        public Builder<T1, T2> start(String name) {
            if (waitingForThen) throw new IllegalStateException("Must call then() before starting new rule");
            currentName = name;
            waitingForWhen = true;
            return this;
        }

        public Builder<T1, T2> when(BiPredicate<T1, T2> predicate) {
            if (!waitingForWhen) throw new IllegalStateException("start() must be called before when()");
            currentPredicate = predicate;
            waitingForWhen = false;
            waitingForThen = true;
            return this;
        }

        public Builder<T1, T2> and(BiPredicate<T1, T2> other) {
            currentPredicate = currentPredicate.and(other);
            return this;
        }

        public Builder<T1, T2> or(BiPredicate<T1, T2> other) {
            currentPredicate = currentPredicate.or(other);
            return this;
        }

        public Builder<T1, T2> then(Runnable action) {
            if (!waitingForThen) throw new IllegalStateException("when() must be followed by then()");
            rules.add(new Rule<>(currentName, index++, currentPredicate, action));
            currentPredicate = null;
            waitingForThen = false;
            return this;
        }

        public PredicateRunner2<T1, T2> build() {
            if (waitingForThen) throw new IllegalStateException("Last rule missing then()");
            return new PredicateRunner2<>(rules);
        }
    }
}
