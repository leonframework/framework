package io.leon.rt.converters;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Map;

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

    public <A, B> B convert(Class<A> fromType, Class<B> toType, Object value) {
        Function<Object, Object> function = converters.get(fromType).get(toType);
        return toType.cast(function.apply(value));
    }

    private void addDefaultConverters() {
        addConverter(String.class, Integer.class, new Function<Object, Object>() {
            @Override
            public Object apply(@Nullable Object input) {
                return Integer.parseInt((String) input);
            }
        });

    }

}
