package io.leon.rt;

import io.leon.rt.converters.Converter;
import org.testng.annotations.Test;

import static io.leon.rt.option.Option.some;
import static org.testng.Assert.assertEquals;

@Test
public class ConverterTest {

    private final Converter converter = new Converter();

    public void stringToString() {
        assertEquals(converter.convert(String.class, String.class, "123"), some("123"));
    }

    public void anythingToString() {
        assertEquals(converter.convert(Integer.class, String.class, 123), some("123"));
        assertEquals(converter.convert(Long.class, String.class, 123L), some("123"));
        assertEquals(converter.convert(Float.class, String.class, 123.0F), some("123.0"));
        assertEquals(converter.convert(Double.class, String.class, 123.0D), some("123.0"));
    }

    public void stringToInteger() {
        assertEquals(converter.convert(String.class, Integer.class, "123"), some(123));
    }

    public void stringToLong() {
        assertEquals(converter.convert(String.class, Long.class, "123"), some(123L));
    }

    public void stringToFloat() {
        assertEquals(converter.convert(String.class, Float.class, "123"), some(123.0F));
    }

    public void stringToDouble() {
        assertEquals(converter.convert(String.class, Double.class, "123"), some(123.0D));
    }

    public void numberToInteger() {
        assertEquals(converter.convert(Double.class, Integer.class, 123.5), some(123));
        assertEquals(converter.convert(Long.class, Integer.class, 123L), some(123));
    }

    public void numberToFloat() {
        assertEquals(converter.convert(Integer.class, Float.class, 123), some(123.0F));
        assertEquals(converter.convert(Long.class, Float.class, 123L), some(123.0F));
        assertEquals(converter.convert(Double.class, Float.class, 123.0D), some(123.0F));
    }

    public void numberToDouble() {
        assertEquals(converter.convert(Integer.class, Double.class, 123), some(123.0));
        assertEquals(converter.convert(Long.class, Double.class, 123L), some(123.0));
        assertEquals(converter.convert(Float.class, Double.class, 123.0F), some(123.0));
    }

}
