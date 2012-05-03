package io.leon.rt.converters;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import io.leon.rt.option.Option;

import javax.annotation.Nullable;
import java.util.Map;

import static io.leon.rt.option.Option.none;
import static io.leon.rt.option.Option.some;

public class Converter {

    // from type -> to type -> Converter(from, to)
    private final Map<Class<?>, Map<Class<?>, Function<Object, Object>>> converters = Maps.newHashMap();

    public Converter() {
        addDefaultConverters();
    }

    public void addConverter(Class<?> fromType, Class<?> toType, Function<Object, Object> converter) {
        if (!converters.containsKey(fromType)) {
            Map<Class<?>, Function<Object, Object>> map = Maps.newHashMap();
            converters.put(fromType, map);
        }
        if (converters.get(fromType).containsKey(toType)) {
            throw new IllegalArgumentException("A converter for " + fromType + " -> " + toType + " is already registered.");
        }
        converters.get(fromType).put(toType, converter);
    }

    public <A, B> Option<B> convert(Class<A> fromType, Class<B> toType, Object value) {
        if (fromType.equals(toType)) {
            // Nothing to do
            return some(toType.cast(value));
        }

        Map<Class<?>, Function<Object, Object>> toConverters = converters.get(fromType);
        if (toConverters == null) {
            // No direct converters found
            if (!fromType.getClass().equals(Object.class)) {
                // check if we can find a converter for a superclass of fromType
                return convert(fromType.getSuperclass(), toType, value);
            } else {
                return none();
            }
        }

        Function<Object, Object> converter = toConverters.get(toType);
        if (converter == null) {
            // We have a fromType match but couldn't find a toType match.
            // Check if a converter for a subtype of toType was registered.
            for (Class<?> aClass : toConverters.keySet()) {
                if (toType.isAssignableFrom(aClass)) {
                    converter = toConverters.get(aClass);
                    break;
                }
            }
        }

        if (converter == null) {
            if (fromType.equals(Object.class)) {
                return none();
            } else {
                // We had a fromType match but couldn't find a converter for a subtype of toType.
                // Start again with the superclass of fromType.
                return convert(fromType.getSuperclass(), toType, value);
            }
        }

        return some(toType.cast(converter.apply(value)));
    }

    private void addDefaultConverters() {
        addConverter(Number.class, Integer.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return ((Number) input).intValue();
            }
        });
        addConverter(Number.class, Float.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return ((Number) input).floatValue();
            }
        });
        addConverter(Number.class, Double.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return ((Number) input).doubleValue();
            }
        });
        addConverter(String.class, Integer.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return Integer.parseInt((String) input);
            }
        });
        addConverter(String.class, Long.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return Long.parseLong((String) input);
            }
        });
        addConverter(String.class, Float.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return Float.parseFloat((String) input);
            }
        });
        addConverter(String.class, Double.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return Double.parseDouble((String) input);
            }
        });
        addConverter(Object.class, String.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return String.valueOf(input);
            }
        });
    }

}
