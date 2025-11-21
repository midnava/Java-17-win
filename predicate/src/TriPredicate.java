@FunctionalInterface
public interface TriPredicate<T1, T2, T3> {
    boolean test(T1 a, T2 b, T3 c);
}
