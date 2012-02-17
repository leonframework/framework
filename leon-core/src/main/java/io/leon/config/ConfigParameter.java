package io.leon.config;


public class ConfigParameter {

    private final String key;
    private final Object value;

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public ConfigParameter(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}
