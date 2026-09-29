package tools.jackson.databind.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0615F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: TypeBindingsTest#testBindingsBasics().
    void bindingsBasicsVpack() {
        TypeBindings bindings = TypeBindings.create(Collection.class, TypeFactory.unknownType());
        assertNotNull(bindings.toString());
        assertSame(Object.class, bindings.getBoundType(0).getRawClass());
        assertNull(bindings.getBoundName(-1));
        assertNull(bindings.getBoundType(-1));
        assertNull(bindings.getBoundName(1));
        assertNull(bindings.getBoundType(1));
        assertFalse(bindings.equals("foo"));
    }

    // Provenance: TypeBindingsTest#testEqualityAndHashCode().
    void equalityAndHashCodeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType stringType = types.constructType(String.class);
        JavaType integerType = types.constructType(Integer.class);
        TypeBindings listString = TypeBindings.create(List.class, stringType);
        TypeBindings listStringUnbound = listString.withUnboundVariable("X");
        TypeBindings iterableString = TypeBindings.create(Iterable.class, stringType);
        TypeBindings mapStringInt = TypeBindings.create(Map.class, stringType, integerType);
        TypeBindings mapIntString = TypeBindings.create(Map.class, integerType, stringType);
        assertEquals("E", listString.getBoundName(0));
        assertEquals("T", iterableString.getBoundName(0));
        assertEquals(listString, iterableString);
        assertEquals(listString.hashCode(), iterableString.hashCode());
        assertEquals(listStringUnbound, listString);
        assertEquals(listStringUnbound.hashCode(), listString.hashCode());
        assertNotEquals(mapStringInt, mapIntString);
        assertNotEquals(mapStringInt.hashCode(), mapIntString.hashCode());

        Object iterableKey = iterableString.asKey(List.class);
        Object listKey = listString.asKey(List.class);
        Object unboundListKey = listStringUnbound.asKey(List.class);
        Object mapKey = mapStringInt.asKey(Map.class);
        Object reversedMapKey = mapIntString.asKey(Map.class);
        assertEquals(iterableKey, listKey);
        assertEquals(iterableKey.hashCode(), listKey.hashCode());
        assertEquals(unboundListKey, listKey);
        assertEquals(unboundListKey.hashCode(), listKey.hashCode());
        assertNotEquals(mapKey, reversedMapKey);
        assertNotEquals(mapKey.hashCode(), reversedMapKey.hashCode());
    }

    // Provenance: TypeBindingsTest#testInnerType().
    void innerTypeVpack() {
        JavaType type = MAPPER.getTypeFactory().constructType(InnerGenericTyping.InnerClass.class);
        assertEquals(MapType.class, type.getClass());
        JavaType keyType = type.getKeyType();
        assertSame(Object.class, keyType.getRawClass());
        JavaType valueType = type.getContentType();
        assertSame(Collection.class, valueType.getRawClass());
        assertSame(Object.class, valueType.getContentType().getRawClass());
    }

    // Provenance: TypeBindingsTest#testInvalidBindings().
    void invalidBindingsVpack() {
        try {
            TypeBindings.create(AbstractType.class, TypeFactory.unknownType());
            fail("Should not pass");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Cannot create TypeBindings"), e.getMessage());
            assertTrue(e.getMessage().contains("class expects 2"), e.getMessage());
        }
    }

    // Provenance: TypeBindingsTest#testRecursiveType().
    void recursiveBindingsTypeVpack() {
        assertNotNull(MAPPER.getTypeFactory().constructType(HashTree.class));
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

    void __invoke_bindingsBasicsVpack() throws Exception {
        try {
            bindingsBasicsVpack();
        } finally {
        }
    }


    void __invoke_equalityAndHashCodeVpack() throws Exception {
        try {
            equalityAndHashCodeVpack();
        } finally {
        }
    }


    void __invoke_innerTypeVpack() throws Exception {
        try {
            innerTypeVpack();
        } finally {
        }
    }


    void __invoke_invalidBindingsVpack() throws Exception {
        try {
            invalidBindingsVpack();
        } finally {
        }
    }


    void __invoke_recursiveBindingsTypeVpack() throws Exception {
        try {
            recursiveBindingsTypeVpack();
        } finally {
        }
    }

}
