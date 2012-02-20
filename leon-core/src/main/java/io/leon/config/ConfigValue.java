package io.leon.config;

/**
 * Value object to hold a configuration parameter.
 *
 * @see {ConfigBinder}
 */
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
