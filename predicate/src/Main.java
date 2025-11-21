public class Main {
    public static void main(String[] args) {
        PredicateRunner1<Integer> runner = new PredicateRunner1.Builder<Integer>()
                .start("is small")
                .when(x -> x < 10)
                .then()
                .start("medium or even")
                .when(x -> x >= 10).and().when(x -> x <= 20)
                .or()
                .when(x -> x % 2 == 0)
                .then()
                .start("big")
                .when(x -> x > 20)
                .then()
                .build();

        int index = runner.run(14);
        System.out.println("Matched rule index = " + index);
        System.out.println("Matched name = " + runner.getRuleName(index));
    }
}