package io.leon.rt;

import java.util.Map;

public class MapNode<K, V> {

    private final RelaxedTypes rt;
    private final Map<K, V> map;

    protected MapNode(RelaxedTypes rt, Map<K, V> map) {
        this.rt = rt;
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    protected MapNode(RelaxedTypes rt, Object mapLike) {
        this.rt = rt;
        this.map = rt.getConverter().convert(mapLike.getClass(), Map.class, mapLike).get();
    }

    public Node<V> get(K key) {
        return rt.node(map.get(key));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapNode mapNode = (MapNode) o;
        return map.equals(mapNode.map) && rt.equals(mapNode.rt);
    }

    @Override
    public int hashCode() {
        int result = rt.hashCode();
        result = 31 * result + map.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MapNode(" + map + ")";
    }
}
