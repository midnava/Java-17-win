import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class PredicateRunner2<T1, T2> {

    private final List<Rule> rules;
    private final Map<Integer, String> indexToName;

    public PredicateRunner2(List<Rule> rules) {
        this.rules = rules;
        this.indexToName = rules.stream()
                .collect(Collectors.toMap(r -> r.index, r -> r.name));
    }

    public int run(T1 a, T2 b) {
        Object[] args = new Object[]{a, b};
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

    public static class Builder<T1, T2> {
        private final List<Rule> rules = new ArrayList<>();
        private String currentName;
        private Predicate<Object[]> currentPredicate;
        private int index = 0;

        private boolean waitingForWhen = false;
        private boolean waitingForThen = false;

        public Builder<T1, T2> start(String name) {
            if (waitingForThen)
                throw new IllegalStateException("Must call then() before starting a new rule.");
            currentName = name;
            waitingForWhen = true;
            return this;
        }

        public Builder<T1, T2> when(BiPredicate<T1, T2> predicate) {
            if (!waitingForWhen)
                throw new IllegalStateException("start() must be called before when().");

            this.currentPredicate = args -> predicate.test((T1) args[0], (T2) args[1]);
            waitingForWhen = false;
            waitingForThen = true;
            return this;
        }

        public Builder<T1, T2> and(BiPredicate<T1, T2> other) {
            currentPredicate = currentPredicate.and(
                    args -> other.test((T1) args[0], (T2) args[1])
            );
            return this;
        }

        public Builder<T1, T2> or(BiPredicate<T1, T2> other) {
            currentPredicate = currentPredicate.or(
                    args -> other.test((T1) args[0], (T2) args[1])
            );
            return this;
        }

        public Builder<T1, T2> then(Runnable action) {
            if (!waitingForThen)
                throw new IllegalStateException("when() must be followed by then().");

            rules.add(new Rule(currentName, index++, currentPredicate, action));
            currentPredicate = null;
            waitingForThen = false;
            return this;
        }

        public PredicateRunner2<T1, T2> build() {
            if (waitingForThen)
                throw new IllegalStateException("Last rule missing then().");

            return new PredicateRunner2<>(rules);
        }
    }
}
