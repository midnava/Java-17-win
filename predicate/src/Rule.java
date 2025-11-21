import java.util.function.Predicate;

public class Rule {
    final String name;
    final int index;
    final Predicate<Object[]> predicate;
    final Runnable action;

    Rule(String name, int index, Predicate<Object[]> predicate, Runnable action) {
        this.name = name;
        this.index = index;
        this.predicate = predicate;
        this.action = action;
    }
}
