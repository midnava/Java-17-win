import java.util.*;
import java.util.stream.Collectors;

public class PredicateRunner3<T1, T2, T3> {

    public static class Rule<T1, T2, T3> {
        final String name;
        final int index;
        final TriPredicate<T1,T2,T3> predicate;
        final Runnable action;

        Rule(String name, int index, TriPredicate<T1,T2,T3> predicate, Runnable action) {
            this.name = name;
            this.index = index;
            this.predicate = predicate;
            this.action = action;
        }
    }

    private final List<Rule<T1,T2,T3>> rules;
    private final Map<Integer,String> indexToName;

    private PredicateRunner3(List<Rule<T1,T2,T3>> rules){
        this.rules = rules;
        this.indexToName = rules.stream().collect(Collectors.toMap(r -> r.index, r -> r.name));
    }

    public int run(T1 a, T2 b, T3 c){
        for(Rule<T1,T2,T3> r : rules){
            if(r.predicate.test(a,b,c)){
                r.action.run();
                return r.index;
            }
        }
        return -1;
    }

    public String getRuleNameByIndex(int index){
        return indexToName.get(index);
    }

    public static class Builder<T1,T2,T3>{
        private final List<Rule<T1,T2,T3>> rules = new ArrayList<>();
        private String currentName;
        private TriPredicate<T1,T2,T3> currentPredicate;
        private int index=0;
        private boolean waitingForWhen=false;
        private boolean waitingForThen=false;

        public Builder<T1,T2,T3> start(String name){
            if(waitingForThen) throw new IllegalStateException("Must call then() before starting new rule");
            currentName=name;
            waitingForWhen=true;
            return this;
        }

        public Builder<T1,T2,T3> when(TriPredicate<T1,T2,T3> pred){
            if(!waitingForWhen) throw new IllegalStateException("start() must be called before when()");
            currentPredicate=pred;
            waitingForWhen=false;
            waitingForThen=true;
            return this;
        }

        public Builder<T1,T2,T3> and(TriPredicate<T1,T2,T3> other){
            currentPredicate=currentPredicate.and(other);
            return this;
        }

        public Builder<T1,T2,T3> or(TriPredicate<T1,T2,T3> other){
            currentPredicate=currentPredicate.or(other);
            return this;
        }

        public Builder<T1,T2,T3> then(Runnable action){
            if(!waitingForThen) throw new IllegalStateException("when() must be followed by then()");
            rules.add(new Rule<>(currentName,index++,currentPredicate,action));
            currentPredicate=null;
            waitingForThen=false;
            return this;
        }

        public PredicateRunner3<T1,T2,T3> build(){
            if(waitingForThen) throw new IllegalStateException("Last rule missing then()");
            return new PredicateRunner3<>(rules);
        }
    }
}
