package codesquad.template.compile.node;

import java.util.HashMap;
import java.util.Map;

public class EvaluatorContext {

    private final Map<String, String> context;

    public EvaluatorContext() {
        this.context = new HashMap<>();
    }

    public String getValue(String variable) {
        return context.getOrDefault(variable, "null");
    }

    public void setValue(String variable, String value) {
        context.put(variable, value);
    }
}
