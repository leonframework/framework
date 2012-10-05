package io.leon.rt;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test
public class ListNodeTest {

    private RelaxedTypes rt;

    private List<Integer> arrayListWithIntegers = Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    private List<Integer> linkedListWithIntegers = Lists.newLinkedList(arrayListWithIntegers);

    private List<Integer> listWithIntegersPlus10 = Lists.newArrayList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

    private Function<Integer, Object> plus10 = new Function<Integer, Object>() {
        @Override
        public Object apply(Integer input) {
            return 10 + input;
        }
    };

    @BeforeTest
    public void beforeTest() {
        rt = new RelaxedTypes();
    }

    public void getByIndex() {
        List<String> list = Lists.newArrayList();
        list.add("a");
        list.add("b");
        list.add("c");

        ListNode<String> node = rt.listNode(list);
        assertEquals(node.get(0).val(), "a");
        assertEquals(node.get(1).val(), "b");
        assertEquals(node.get(2).val(), "c");
    }

    public void mapOverListOfIntegers() {
        ListNode<Integer> listNode = rt.listNode(arrayListWithIntegers);
        Collection<Object> collectionPlus10 = listNode.map(plus10);
        assertEquals(collectionPlus10, listWithIntegersPlus10);
    }

    public void mapPreservesTypeOfCollection() {
        Collection<Object> mapOverArrayList = rt.listNode(arrayListWithIntegers).map(plus10);
        assertEquals(mapOverArrayList.getClass(), arrayListWithIntegers.getClass());

        Collection<Object> mapOverLinkedList = rt.listNode(linkedListWithIntegers).map(plus10);
        assertEquals(mapOverLinkedList.getClass(), linkedListWithIntegers.getClass());
    }

    public void asListOf() {
        ArrayList<String> listWithStrings = Lists.newArrayList("1", "2", "3");
        ListNode<Integer> integerListNode = rt.listNode(listWithStrings).asListOf(Integer.class);
        assertEquals(integerListNode.val(), Lists.newArrayList(1, 2, 3));
    }

}
