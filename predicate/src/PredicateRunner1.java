import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class PredicateRunner1<T1> {

    private final List<Rule> rules;
    private final Map<Integer, String> indexToName;

    public PredicateRunner1(List<Rule> rules) {
        this.rules = rules;
        this.indexToName = rules.stream()
                .collect(Collectors.toMap(r -> r.index, r -> r.name));
    }

    public int run(T1 a) {
        Object[] args = new Object[]{a};
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

    public static class Builder<T1> {
        private final List<Rule> rules = new ArrayList<>();
        private String currentName;
        private Predicate<Object[]> currentPredicate;
        private int index = 0;

        private boolean waitingForWhen = false;
        private boolean waitingForThen = false;

        public Builder<T1> start(String name) {
            if (waitingForThen)
                throw new IllegalStateException("Must call then() before starting a new rule.");

            currentName = name;
            waitingForWhen = true;
            return this;
        }

        public Builder<T1> when(Predicate<T1> predicate) {
            if (!waitingForWhen)
                throw new IllegalStateException("start() must be called before when().");

            this.currentPredicate = args -> predicate.test((T1) args[0]);
            waitingForWhen = false;
            waitingForThen = true;
            return this;
        }

        public Builder<T1> and(Predicate<T1> other) {
            currentPredicate = currentPredicate.and(args -> other.test((T1) args[0]));
            return this;
        }

        public Builder<T1> or(Predicate<T1> other) {
            currentPredicate = currentPredicate.or(args -> other.test((T1) args[0]));
            return this;
        }

        public Builder<T1> then(Runnable action) {
            if (!waitingForThen)
                throw new IllegalStateException("when() must be followed by then().");

            rules.add(new Rule(currentName, index++, currentPredicate, action));
            currentPredicate = null;
            waitingForThen = false;
            return this;
        }

        public PredicateRunner1<T1> build() {
            if (waitingForThen)
                throw new IllegalStateException("Last rule missing then().");

            return new PredicateRunner1<>(rules);
        }
    }
}
