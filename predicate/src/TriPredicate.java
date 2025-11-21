@FunctionalInterface
public interface TriPredicate<T1, T2, T3> {
    boolean test(T1 a, T2 b, T3 c);

    default TriPredicate<T1,T2,T3> and(TriPredicate<T1,T2,T3> other) {
        return (a,b,c) -> this.test(a,b,c) && other.test(a,b,c);
    }

    default TriPredicate<T1,T2,T3> or(TriPredicate<T1,T2,T3> other) {
        return (a,b,c) -> this.test(a,b,c) || other.test(a,b,c);
    }
}
