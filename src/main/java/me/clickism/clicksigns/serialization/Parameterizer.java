package me.clickism.clicksigns.serialization;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Parameterizer {
    private static final String FORMAT = "{%s}";

    protected final Map<String, Object> params = new HashMap<>();

    public Parameterizer put(String key, @NotNull Object value) {
        this.params.put(key, value);
        return this;
    }

    public Parameterizer putAll(Map<String, Object> map) {
        this.params.putAll(map);
        return this;
    }

    public String apply(String string) {
        String result = string;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = String.format(FORMAT, entry.getKey());
            result = result.replace(placeholder, entry.getValue().toString());
        }
        return result;
    }
}
