package io.leon.rt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class RtTest {

    private Rt rt;

    @BeforeTest
    public void beforeTest() {
        rt = new Rt();
    }

    public void val() {
        assertEquals(rt.of("test").val(), "test");
    }

    public void keepTypeWhenPossible() {
        String in = "test";
        String out = rt.of(in).val();
        assertTrue(in.equals(out), "Dummy test to ensure that the lines above compile.");
    }

    public void mapGet() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("key1", "value1");
        map.put("key2", 2);

        assertEquals(rt.of(map).get("key1").val(), map.get("key1"));
    }

    public void mapMapGet() {
        Map<String, Object> map1 = Maps.newHashMap();
        Map<String, Object> map2 = Maps.newHashMap();
        map1.put("key1", map2);
        map2.put("key2", "value2");

        assertEquals(rt.of(map1).get("key1").get("key2").val(), map2.get("key2"));
    }

    public void getIndexList() {
        List<String> list = Lists.newArrayList();
        list.add("a");
        list.add("b");
        list.add("c");

        Node node = rt.of((Object) list);
        assertEquals(node.get(0).val(), "a");
        assertEquals(node.get(1).val(), "b");
        assertEquals(node.get(2).val(), "c");
    }

    public void mapMapListGetIndexToInt() {
        Map<String, Object> map1 = Maps.newHashMap();
        Map<String, Object> map2 = Maps.newHashMap();
        List<String> list = Lists.newArrayList();
        list.add("1");
        list.add("2");
        list.add("3");

        map2.put("key2", list);
        map1.put("key1", map2);

        Node node = rt.of(map1);
        assertEquals(node.get("key1").get("key2").get(0).valInt(), 1);
        assertEquals(node.get("key1").get("key2").get(1).valInt(), 2);
        assertEquals(node.get("key1").get("key2").get(2).valInt(), 3);
    }
}