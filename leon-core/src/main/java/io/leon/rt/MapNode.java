package io.leon.rt;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.util.Map;

public class MapNode<K, V> {

    private final RelaxedTypes rt;
    private final Map<K, V> map;

    protected MapNode(RelaxedTypes rt, Map<K, V> map) {
        this.rt = rt;
        this.map = map;
    }

    public Map<K, V> val() {
        return map;
    }

    public Node<V> get(K key) {
        return rt.node(map.get(key));
    }

    @SuppressWarnings("unchecked")
    public <A, B> MapNode<A, B> map(Function<Pair<K, V>, Pair<A, B>> fn) {
        Map<A, B> newMap;
        try {
            Constructor<? extends Map> constructor = map.getClass().getConstructor();
            newMap = constructor.newInstance();
        } catch (Exception e) {
            newMap = Maps.newHashMapWithExpectedSize(map.size());
        }
        for (K key : map.keySet()) {
            V value = map.get(key);
            Pair<A, B> result = fn.apply(Pair.of(key, value));
            newMap.put(result.getFirst(), result.getSecond());
        }
        return rt.mapNode(newMap);
    }

    public <A, B> MapNode<A, B> asMapOf(final Class<A> keyType, final Class<B> valueType) {
        return map(new Function<Pair<K, V>, Pair<A, B>>() {
            @Override
            public Pair<A, B> apply(Pair<K, V> input) {
                A newKey = rt.getConverter().convert(
                        input.getFirst().getClass(), keyType, input.getFirst()).getOrThrowException();
                B newValue = rt.getConverter().convert(
                        input.getSecond().getClass(), valueType, input.getSecond()).getOrThrowException();
                return Pair.of(newKey, newValue);
            }
        });
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
