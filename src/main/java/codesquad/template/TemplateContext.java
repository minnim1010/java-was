package codesquad.template;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TemplateContext {

    private final Map<String, Object> context;
    private final TemplateContext parentContext;

    public TemplateContext() {
        this.parentContext = null;
        this.context = new HashMap<>();
    }

    public TemplateContext(TemplateContext parentContext) {
        this.parentContext = parentContext;
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
        if (parentContext != null) {
            return parentContext.getValue(key);
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
