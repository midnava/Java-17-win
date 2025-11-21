public class Main {
    public static void main(String[] args) {

        // -------- Runner1 ----------
        PredicateRunner1<Integer> r1 = new PredicateRunner1.Builder<Integer>()
                .start("positive")
                .when(x -> x > 0)
                .then(() -> System.out.println("Positive number"))
                .start("zero")
                .when(x -> x == 0)
                .then(() -> System.out.println("Zero"))
                .build();

        int idx1 = r1.run(10);
        System.out.println("Triggered: " + r1.getRuleNameByIndex(idx1));


        // -------- Runner2 ----------
        PredicateRunner2<Integer, Integer> r2 = new PredicateRunner2.Builder<Integer, Integer>()
                .start("sum>50")
                .when((a, b) -> a + b > 50)
                .then(() -> System.out.println("Big sum"))
                .start("equal")
                .when(Integer::equals)
                .then(() -> System.out.println("Equal numbers"))
                .build();

        int idx2 = r2.run(30, 25);
        System.out.println("Triggered: " + r2.getRuleNameByIndex(idx2));


        // -------- Runner3 ----------
        PredicateRunner3<Integer, Integer, Integer> r3 = new PredicateRunner3.Builder<Integer, Integer, Integer>()
                .start("all positive")
                .when((a, b, c) -> a > 0 && b > 0 && c > 0)
                .then(() -> System.out.println("All positive"))
                .start("sum>100")
                .when((a, b, c) -> a + b + c > 100)
                .then(() -> System.out.println("Big sum of three"))
                .build();

        int idx3 = r3.run(10, 20, 30);
        System.out.println("Triggered: " + r3.getRuleNameByIndex(idx3));
    }
}
