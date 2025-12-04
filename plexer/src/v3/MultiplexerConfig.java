package v3;

import java.util.*;

public class MultiplexerConfig {

    private final Map<String, InRule> inRules;
    private final Map<String, OutRule> outRules;

    public MultiplexerConfig(Map<String, InRule> inRules,
                             Map<String, OutRule> outRules) {
        this.inRules = inRules;
        this.outRules = outRules;
    }

    public InRule getInRule(String msgType) {
        return inRules.get(msgType);
    }

    public OutRule getOutRule(String msgType) {
        return outRules.get(msgType);
    }

    public static class Builder {

        private final Map<String, InRule> in = new HashMap<>();
        private final Map<String, OutRule> out = new HashMap<>();

        public Builder addInRule(InRule rule) {
            in.put(rule.getMsgType(), rule);
            return this;
        }

        public Builder addOutRule(OutRule rule) {
            out.put(rule.getMsgType(), rule);
            return this;
        }

        public MultiplexerConfig build() {
            return new MultiplexerConfig(in, out);
        }
    }
}
