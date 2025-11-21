import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class PredicateRunner3<T1, T2, T3> {

    private final List<Rule> rules;
    private final Map<Integer, String> indexToName;

    public PredicateRunner3(List<Rule> rules) {
        this.rules = rules;
        this.indexToName = rules.stream()
                .collect(Collectors.toMap(r -> r.index, r -> r.name));
    }

    public int run(T1 a, T2 b, T3 c) {
        Object[] args = new Object[]{a, b, c};
        for (Rule rule : rules) {
            if (rule.predicate.test(args)) {
                rule.action.run();
                return rule.index;
            }
        }
        return -1;
    }

    public String getRuleNameByIndex(int index) {
        return indexToName.get(index);
    }

    // ---------------- BUILDER ----------------

    public static class Builder<T1, T2, T3> {
        private final List<Rule> rules = new ArrayList<>();
        private String currentName;
        private Predicate<Object[]> currentPredicate;
        private int index = 0;

        private boolean waitingForWhen = false;
        private boolean waitingForThen = false;

        public Builder<T1, T2, T3> start(String name) {
            if (waitingForThen)
                throw new IllegalStateException("Must call then() before starting a new rule.");

            currentName = name;
            waitingForWhen = true;

            return this;
        }

        public Builder<T1, T2, T3> when(TriPredicate<T1, T2, T3> predicate) {
            if (!waitingForWhen)
                throw new IllegalStateException("start() must be called before when().");

            this.currentPredicate = args -> predicate.test(
                    (T1) args[0], (T2) args[1], (T3) args[2]
            );
            waitingForWhen = false;
            waitingForThen = true;
            return this;
        }

        public Builder<T1, T2, T3> and(TriPredicate<T1, T2, T3> other) {
            currentPredicate = currentPredicate.and(
                    args -> other.test((T1) args[0], (T2) args[1], (T3) args[2])
            );
            return this;
        }

        public Builder<T1, T2, T3> or(TriPredicate<T1, T2, T3> other) {
            currentPredicate = currentPredicate.or(
                    args -> other.test((T1) args[0], (T2) args[1], (T3) args[2])
            );
            return this;
        }

        public Builder<T1, T2, T3> then(Runnable action) {
            if (!waitingForThen)
                throw new IllegalStateException("when() must be followed by then().");

            rules.add(new Rule(currentName, index++, currentPredicate, action));
            currentPredicate = null;
            waitingForThen = false;
            return this;
        }

        public PredicateRunner3<T1, T2, T3> build() {
            if (waitingForThen)
                throw new IllegalStateException("Last rule missing then().");

            return new PredicateRunner3<>(rules);
        }
    }
}
