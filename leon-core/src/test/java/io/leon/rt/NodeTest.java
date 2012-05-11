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
public class NodeTest {

    private RelaxedTypes rt;

    @BeforeTest
    public void beforeTest() {
        rt = new RelaxedTypes();
    }

    public void val() {
        assertEquals(rt.node("test").val(), "test");
    }

    public void keepTypeWhenPossible() {
        String in = "test";
        String out = rt.node(in).val();
        assertTrue(in.equals(out), "Dummy test to ensure that the lines above compile.");
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

    public void getIndexList() {
        List<String> list = Lists.newArrayList();
        list.add("a");
        list.add("b");
        list.add("c");

        Node node = rt.node(list);

        assertEquals(node.asList().get(0).val(), "a");
        assertEquals(node.asList().get(1).val(), "b");
        assertEquals(node.asList().get(2).val(), "c");
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

        Node<?> node = rt.node(map1);
        assertEquals(node.asMap().get("key1").asMap().get("key2").asList().get(0).asInt(), 1);
        assertEquals(node.asMap().get("key1").asMap().get("key2").asList().get(1).asInt(), 2);
        assertEquals(node.asMap().get("key1").asMap().get("key2").asList().get(2).asInt(), 3);
    }
}
