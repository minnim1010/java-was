package codesquad.template.compile.node;

import java.util.HashMap;
import java.util.Map;

public class EvaluatorContext {

    private final Map<String, Object> context;

    public EvaluatorContext() {
        this.context = new HashMap<>();
    }

    public Object getValue(String key) {
        return context.getOrDefault(key, "null");
    }

    public void setValue(String variable, Object value) {
        context.put(variable, value);
    }
}
