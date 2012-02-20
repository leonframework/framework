package io.leon.config;


public class ConfigValue {

    private final String key;
    private final String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public ConfigValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
