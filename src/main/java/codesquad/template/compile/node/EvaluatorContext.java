package codesquad.template.compile.node;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EvaluatorContext {

    private final Map<String, Object> context;

    public EvaluatorContext() {
        this.context = new HashMap<>();
    }

    public void setValue(String variable, Object value) {
        context.put(variable, value);
    }

    public Object getValue(String key) {
        if (key.contains(".")) {
            return getNestedValue(key);
        }
        if (context.containsKey(key)) {
            return context.get(key);
        }
        return "null";
    }

    private Object getNestedValue(String key) {
        String[] parts = key.split("\\.");
        Object currentObject = getValue(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            currentObject = getPropertyValue(currentObject, parts[i]);
            if (currentObject == null) {
                return null;
            }
        }
        return currentObject;
    }

    private Object getPropertyValue(Object obj, String propertyName) {
        try {
            Method method = obj.getClass().getMethod("get" + capitalize(propertyName));
            return method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Cannot get property " + propertyName + " from " + obj, e);
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
