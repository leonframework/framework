package io.leon.rt;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

@Test
public class RTTest {

    public void val() {
        assertEquals(RT.of("test").val(), "test");
    }

    public void valStringWithInt() {
        assertEquals(RT.of("1").valString(), "1");
    }

    public void valStringWithDouble() {
        assertEquals(RT.of(1.2).valString(), "1.2");
    }

    public void valStringWithLong() {
        assertEquals(RT.of(1L).valString(), "1");
    }

    public void valIntWithInt() {
        assertEquals(RT.of(123).valInt(), 123);
    }

    public void valIntWitString() {
        assertEquals(RT.of("123").valInt(), 123);
    }

    public void mapGet() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("key1", "value1");
        map.put("key2", 2);

        assertEquals(RT.of(map).get("key1").val(), map.get("key1"));
    }

    public void mapMapGet() {
        Map<String, Object> map1 = Maps.newHashMap();
        Map<String, Object> map2 = Maps.newHashMap();
        map1.put("key1", map2);
        map2.put("key2", "value2");

        assertEquals(RT.of(map1).get("key1").get("key2").val(), map2.get("key2"));
    }

    public void getIndexList() {
        List<String> list = Lists.newArrayList();
        list.add("a");
        list.add("b");
        list.add("c");

        RT rtList = RT.of(list);
        assertEquals(rtList.get(0).val(), "a");
        assertEquals(rtList.get(1).val(), "b");
        assertEquals(rtList.get(2).val(), "c");
    }

    public void mapMapListGetIndexToInt() {
        Map<String, Object> map1 = Maps.newHashMap();
        Map<String, Object> map2 = Maps.newHashMap();
        List<String> list = Lists.newArrayList();
        list.add("1");
        list.add("2");
        list.add("3");

        map1.put("key1", map2);
        map2.put("key2", list);

        RT rtMap = RT.of(map1);
        assertEquals(rtMap.get("key1").get("key2").get(0).valInt(), 1);
        assertEquals(rtMap.get("key1").get("key2").get(1).valInt(), 2);
        assertEquals(rtMap.get("key1").get("key2").get(2).valInt(), 3);
    }
}
