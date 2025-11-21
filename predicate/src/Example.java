public class Example {

    public static void main(String[] args) {

        // ---------------- Runner1 Example ----------------
        var r1 = new PredicateRunner1.Builder<Integer>()
                .start("is positive")
                    .when(x -> x > 0)
                .then(() -> System.out.println("Positive"))
                .start("is zero")
                    .when(x -> x == 0)
                .then(() -> System.out.println("Zero"))
                .build();

        int idx1 = r1.run(15);
        System.out.println("Rule: " + r1.getRuleNameByIndex(idx1));


        // ---------------- Runner2 Example ----------------
        var r2 = new PredicateRunner2.Builder<Integer, Integer>()
                .start("sum > 50")
                    .when((a, b) -> a + b > 50)
                .then(() -> System.out.println("Big sum"))
                .start("equal numbers")
                    .when(Integer::equals)
                .then(() -> System.out.println("Equal"))
                .build();

        int idx2 = r2.run(30, 25);
        System.out.println("Rule: " + r2.getRuleNameByIndex(idx2));


        // ---------------- Runner3 Example ----------------
        var r3 = new PredicateRunner3.Builder<Integer, Integer, Integer>()
                .start("all > 0")
                    .when((a, b, c) -> a > 0 && b > 0 && c > 0)
                .then(() -> System.out.println("All positive"))
                .start("sum > 100")
                    .when((a, b, c) -> a + b + c > 100)
                .then(() -> System.out.println("Big triple sum"))
                .build();

        int idx3 = r3.run(10, 20, 30);
        System.out.println("Rule: " + r3.getRuleNameByIndex(idx3));
    }
}
