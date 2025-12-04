package v2;

import java.util.*;

public class MultiplexerConfig {
    private final Map<String, InRule> inRules;
    private final Map<String, OutRule> outRules;

    private MultiplexerConfig(Builder b) {
        this.inRules = b.inRules;
        this.outRules = b.outRules;
    }

    public Map<String, InRule> getInRules() { return inRules; }
    public Map<String, OutRule> getOutRules() { return outRules; }

    public static class Builder {
        private final Map<String, InRule> inRules = new HashMap<>();
        private final Map<String, OutRule> outRules = new HashMap<>();

        public Builder addInRule(InRule r) {
            inRules.put(r.getMsgType(), r);
            return this;
        }
        public Builder addOutRule(OutRule r) {
            outRules.put(r.getMsgType(), r);
            return this;
        }

        public MultiplexerConfig build() {
            return new MultiplexerConfig(this);
        }
    }
}
