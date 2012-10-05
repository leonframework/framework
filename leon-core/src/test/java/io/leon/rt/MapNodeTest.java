package io.leon.rt;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;

@Test
public class MapNodeTest {

    private RelaxedTypes rt;

    @BeforeTest
    public void beforeTest() {
        rt = new RelaxedTypes();
    }

    public void mapGet() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("key1", "value1");
        map.put("key2", 2);
        assertEquals(rt.mapNode(map).get("key1").val(), map.get("key1"));
    }

    public void mapMapGet() {
        Map<String, Object> map1 = Maps.newHashMap();
        Map<String, Object> map2 = Maps.newHashMap();
        map1.put("key1", map2);
        map2.put("key2", "value2");
        assertEquals(rt.mapNode(map1).get("key1").asMap().get("key2").val(), map2.get("key2"));
    }

    public void map() {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        MapNode<String, Integer> mapped = rt.mapNode(map).map(new Function<Pair<String, Integer>, Pair<String, Integer>>() {
            @Override
            public Pair<String, Integer> apply(Pair<String, Integer> input) {
                return Pair.of(input.getFirst(), input.getSecond() + 10);
            }
        });
        assertEquals((int) mapped.get("a").val(), 11);
        assertEquals((int) mapped.get("b").val(), 12);
        assertEquals((int) mapped.get("c").val(), 13);
    }

    public void asMapOf() {
        Map<Object, Object> map = Maps.newHashMap();
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        MapNode<String, Integer> mapStringInt = rt.mapNode(map).asMapOf(String.class, Integer.class);
        assertEquals((int) mapStringInt.get("a").val(), 1);
        assertEquals((int) mapStringInt.get("b").val(), 2);
        assertEquals((int) mapStringInt.get("c").val(), 3);

    }


}
