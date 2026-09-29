package tools.jackson.databind.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.TypeResolverBuilder;
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0615F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: RecursiveTypeTest#testJavaTypeToString().
    void javaTypeToStringVpack() {
        String description = MAPPER.getTypeFactory().constructType(DataDefinition.class).toString();
        assertNotNull(description);
        assertTrue(description.contains("map type"), description);
        assertTrue(description.contains("recursive type"), description);
    }

    // Provenance: RecursiveTypeTest#testMutuallyRecursiveGenericBounds5857().
    void mutuallyRecursiveGenericBounds5857Vpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType type1 = types.constructType(new TypeReference<List<RuleSetRevisionDTO<?, ?>>>() { });
        assertNotNull(type1);
        JavaType type2 = types.constructCollectionType(List.class, RuleSetRevisionDTO.class);
        assertNotNull(type2);
    }

    // Provenance: RecursiveTypeTest#testRecursive1658().
    void recursive1658Vpack() throws Exception {
        Tree1658<String> input = new Tree1658<>(Arrays.asList("hello", "world"));
        TypeResolverBuilder<?> typer = new StdTypeResolverBuilder(JsonTypeInfo.Id.CLASS,
                JsonTypeInfo.As.PROPERTY, null);
        ObjectMapper typedMapper = VPackMapper.builder().setDefaultTyping(typer).build();
        byte[] encoded = typedMapper.writeValueAsBytes(input);
        Tree1658<?> output = typedMapper.readValue(encoded, Tree1658.class);
        assertNotNull(output);

        JavaType resolved = typedMapper.getTypeFactory()
                .constructType(new TypeReference<Tree1658<String>>() { });
        String namePath = Tree1658.class.getName().replace('.', '/');
        assertEquals("L" + namePath + ";", resolved.getErasedSignature());
        assertEquals("L" + namePath + "<Ljava/lang/String;L" + namePath + ";>;",
                resolved.getGenericSignature());
    }

    // Provenance: RecursiveTypeTest#testRecursivePair().
    void recursivePairVpack() throws Exception {
        JavaType type = MAPPER.constructType(ImmutablePair.class);
        assertNotNull(type);
        assertSame(ImmutablePair.class, type.getRawClass());
        List<ImmutablePair<String, Double>> values = new ArrayList<>();
        values.add(ImmutablePair.of("Hello World!", 123d));
        assertNotNull(MAPPER.writeValueAsBytes(values));
    }

    // Provenance: RecursiveTypeTest#testRecursiveType().
    void recursiveTypeVpack() {
        JavaType type = MAPPER.getTypeFactory().constructType(HashTree.class);
        assertNotNull(type);
    }

    // Provenance: RecursiveTypeTest#testSuperClassWithReferencedJavaType().
    void superClassWithReferencedJavaTypeVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        types.constructType(Base.class);
        JavaType subType = types.constructType(Sub.class);
        JavaType baseTypeFromSub = subType.getSuperClass();
        assertNotNull(baseTypeFromSub.getSuperClass());
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

    void __invoke_javaTypeToStringVpack() throws Exception {
        try {
            javaTypeToStringVpack();
        } finally {
        }
    }


    void __invoke_mutuallyRecursiveGenericBounds5857Vpack() throws Exception {
        try {
            mutuallyRecursiveGenericBounds5857Vpack();
        } finally {
        }
    }


    void __invoke_recursive1658Vpack() throws Exception {
        try {
            recursive1658Vpack();
        } finally {
        }
    }


    void __invoke_recursivePairVpack() throws Exception {
        try {
            recursivePairVpack();
        } finally {
        }
    }


    void __invoke_recursiveTypeVpack() throws Exception {
        try {
            recursiveTypeVpack();
        } finally {
        }
    }


    void __invoke_superClassWithReferencedJavaTypeVpack() throws Exception {
        try {
            superClassWithReferencedJavaTypeVpack();
        } finally {
        }
    }

}
