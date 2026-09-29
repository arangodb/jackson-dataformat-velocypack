package tools.jackson.databind.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.TypeFactory;

import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0615F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: TypeFactoryTest#testArrays().
    void arraysVpack() {
        Class<?>[] classes = {
                boolean[].class, byte[].class, char[].class, short[].class,
                int[].class, long[].class, float[].class, double[].class,
                String[].class, Object[].class, java.util.Calendar[].class
        };
        TypeFactory types = MAPPER.getTypeFactory();
        for (Class<?> arrayClass : classes) {
            assertSame(arrayClass, types.constructType(arrayClass).getRawClass());
            assertSame(arrayClass, types.constructArrayType(arrayClass.getComponentType()).getRawClass());
        }
    }
@SuppressWarnings("serial")
    static class HashTree<K, V> extends HashMap<K, HashTree<K, V>> { }
@SuppressWarnings("serial")
    static class DataDefinition extends HashMap<String, DataDefinition> {
        public DataDefinition definition;
        public DataDefinition elements;
        public String regex;
        public boolean required;
        public String type;
    }
static class RuleSetRevisionDTO<RS extends RuleSetDTO<?>, RSR extends RuleSetRuleDTO<?, ?>> {
        public RS ruleSet;
        public List<RSR> rules;
    }
static class RuleSetDTO<RSRV extends RuleSetRevisionDTO<?, ?>> {
        public List<RSRV> revisions;
    }
static class RuleSetRuleDTO<RR, RS extends RuleSetRevisionDTO<?, ?>> {
        public RR rule;
        public RS revision;
    }
@SuppressWarnings("serial")
    static class Tree1658<T> extends HashMap<T, Tree1658<T>> {
        Tree1658() { }
        Tree1658(List<T> children) {
            for (T child : children) {
                put(child, new Tree1658<>());
            }
        }
        public List<Tree1658<T>> getLeafTrees() { return null; }
    }
public interface Ability<T> { }
public static final class ImmutablePair<L, R>
            implements Map.Entry<L, R>, Ability<ImmutablePair<L, R>> {
        public final L key;
        public final R value;
        ImmutablePair(L key, R value) { this.key = key; this.value = value; }
        @Override public L getKey() { return key; }
        @Override public R getValue() { return value; }
        @Override public R setValue(R value) { throw new UnsupportedOperationException(); }
        static <L, R> ImmutablePair<L, R> of(L left, R right) { return new ImmutablePair<>(left, right); }
    }
interface IFace<T> { }
static class Base implements IFace<Sub> { }
static class Sub extends Base { }
static class AbstractType<T, U> { }
static class InnerGenericTyping<K, V> extends java.util.AbstractMap<K, Collection<V>> {
        @Override public java.util.Set<Map.Entry<K, Collection<V>>> entrySet() { return null; }
        class InnerClass extends java.util.AbstractMap<K, Collection<V>> {
            @Override public java.util.Set<Map.Entry<K, Collection<V>>> entrySet() { return null; }
        }
    }

    void __invoke_arraysVpack() throws Exception {
        try {
            arraysVpack();
        } finally {
        }
    }

}
