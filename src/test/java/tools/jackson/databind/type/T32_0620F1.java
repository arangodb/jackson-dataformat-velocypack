package tools.jackson.databind.type;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.type.TypeModifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0620F1 {
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: TypeResolutionTest#testMaps().
    void resolvesInheritedMapBindingsVpack() throws Exception {
        TypeFactory types = MAPPER.getTypeFactory();
        JavaType resolved = types.constructType(new TypeReference<LongValuedMap<String>>() { });
        assertSame(LongValuedMap.class, resolved.getRawClass());
        assertEquals(MapType.class, resolved.getClass());
        assertEquals(String.class, resolved.getKeyType().getRawClass());
        assertEquals(Long.class, resolved.getContentType().getRawClass());

        LongValuedMap<String> value = new LongValuedMap<>();
        value.put("count", 42L);
        LongValuedMap<String> decoded = MAPPER.readerFor(resolved)
                .readValue(MAPPER.writeValueAsBytes(value));
        assertEquals(Map.of("count", 42L), decoded);
    }

    // Provenance: TypeResolutionTest#testListViaTypeRef().
    void resolvesListViaTypeReferenceVpack() throws Exception {
        JavaType resolved = MAPPER.getTypeFactory()
                .constructType(new TypeReference<MyLongList<Integer>>() { });
        assertSame(MyLongList.class, resolved.getRawClass());
        assertEquals(CollectionType.class, resolved.getClass());
        assertEquals(Long.class, resolved.getContentType().getRawClass());
        MyLongList<Integer> decoded = MAPPER.readerFor(resolved)
                .readValue(MAPPER.writeValueAsBytes(List.of(7L, 9L)));
        assertEquals(List.of(7L, 9L), decoded);
    }

    // Provenance: TypeResolutionTest#testListViaClass().
    void resolvesListViaClassVpack() {
        JavaType resolved = MAPPER.getTypeFactory().constructType(LongList.class);
        assertSame(LongList.class, resolved.getRawClass());
        assertEquals(Long.class, resolved.getContentType().getRawClass());
    }

    // Provenance: TypeResolutionTest#testGeneric().
    void resolvesGenericSuperclassViaClassAndTypeReferenceVpack() {
        TypeFactory types = MAPPER.getTypeFactory();
        assertEquals(Double.class, types.constructType(DoubleRange.class)
                .findSuperType(Range.class).containedType(0).getRawClass());
        assertEquals(Double.class, types.constructType(new TypeReference<DoubleRange>() { })
                .findSuperType(Range.class).containedType(0).getRawClass());
    }

    // Provenance: TypeResolutionTest#specificTypeUnchanged().
    void specificWildcardFieldTypeUnchangedVpack() throws Exception {
        Type generic = Holder.class.getDeclaredField("specificWrapper").getGenericType();
        JavaType resolved = MAPPER.getTypeFactory().constructType(generic);
        assertSame(MessageWrapper.class, resolved.getRawClass());
        assertEquals(1, resolved.containedTypeCount());
        assertEquals(EmailSettings.class, resolved.containedType(0).getRawClass());
    }

    // Provenance: TypeResolutionTest#serializationPreservesTypeInfo().
    void serializationPreservesPolymorphicTypeInfoVpack() throws Exception {
        MessageWrapper<EmailSettings> value = new MessageWrapper<>(
                new EmailSettings("me@me.com"), "Sample Message");
        Type generic = Holder.class.getDeclaredField("wildcardWrapper").getGenericType();
        JavaType resolved = MAPPER.getTypeFactory().constructType(generic);
        byte[] encoded = MAPPER.writerFor(resolved).writeValueAsBytes(value);
        MessageWrapper<?> decoded = MAPPER.readerFor(resolved).readValue(encoded);
        assertSame(EmailSettings.class, decoded.settings().getClass());
        assertEquals(new EmailSettings("me@me.com"), decoded.settings());
        assertEquals("Sample Message", decoded.message());
    }

    // Provenance: TypeResolutionTest#unboundedTypeParamStaysObject().
    void unboundedWildcardResolvesToObjectVpack() throws Exception {
        Type generic = PairHolder.class.getDeclaredField("wildcardPair").getGenericType();
        JavaType resolved = MAPPER.getTypeFactory().constructType(generic);
        assertSame(Pair.class, resolved.getRawClass());
        assertEquals(2, resolved.containedTypeCount());
        assertSame(String.class, resolved.containedType(0).getRawClass());
        assertSame(Object.class, resolved.containedType(1).getRawClass());
    }

    // Provenance: TypeResolutionTest#mutuallyRecursiveBoundsNoStackOverflow().
    void mutuallyRecursiveBoundsTypeReferenceVpack() {
        JavaType resolved = MAPPER.getTypeFactory().constructType(
                new TypeReference<List<RuleSetRevision<?, ?>>>() { });
        assertNotNull(resolved);
        assertSame(List.class, resolved.getRawClass());
        assertSame(RuleSetRevision.class, resolved.getContentType().getRawClass());
    }

    // Provenance: TypeResolutionTest#mutuallyRecursiveBoundsConstructCollectionType().
    void mutuallyRecursiveBoundsCollectionTypeVpack() {
        JavaType resolved = MAPPER.getTypeFactory()
                .constructCollectionType(List.class, RuleSetRevision.class);
        assertNotNull(resolved);
        assertSame(List.class, resolved.getRawClass());
        assertSame(RuleSetRevision.class, resolved.getContentType().getRawClass());
    }
private static TypeModifier noopModifier() {
        return new TypeModifier() {
            @Override
            public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context,
                    TypeFactory typeFactory) {
                return type;
            }
        };
    }
private static final class ClassLoaderProbe extends ClassLoader {
        String requested;

        ClassLoaderProbe(ClassLoader parent) { super(parent); }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            requested = name;
            return super.loadClass(name, resolve);
        }
    }
static class AClass { }
static class LongValuedMap<K> extends HashMap<K, Long> { }
static class GenericList<X> extends ArrayList<X> { }
static class GenericList2<Y> extends GenericList<Y> { }
static class LongList extends GenericList2<Long> { }
static class MyLongList<T> extends LongList { }
static class Range<E extends Comparable<E>> { Range(E start, E end) { } }
static class DoubleRange extends Range<Double> { DoubleRange() { super(null, null); } }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(value = EmailSettings.class, name = "EMAIL") })
    interface Settings { }
record EmailSettings(String email) implements Settings { }
record MessageWrapper<T extends Settings>(T settings, String message) { }
static class Holder {
        MessageWrapper<?> wildcardWrapper;
        MessageWrapper<EmailSettings> specificWrapper;
    }
static class Pair<L, R> { }
static class PairHolder { Pair<String, ?> wildcardPair; }
abstract static class RuleSetRevision<RS extends RuleSet<?>,
            RSR extends RuleSetRule<?, ?>> { }
abstract static class RuleSet<RSRV extends RuleSetRevision<?, ?>> { }
abstract static class RuleSetRule<RR, RS extends RuleSetRevision<?, ?>> { }

    void __invoke_resolvesInheritedMapBindingsVpack() throws Exception {
        try {
            resolvesInheritedMapBindingsVpack();
        } finally {
        }
    }


    void __invoke_resolvesListViaTypeReferenceVpack() throws Exception {
        try {
            resolvesListViaTypeReferenceVpack();
        } finally {
        }
    }


    void __invoke_resolvesListViaClassVpack() throws Exception {
        try {
            resolvesListViaClassVpack();
        } finally {
        }
    }


    void __invoke_resolvesGenericSuperclassViaClassAndTypeReferenceVpack() throws Exception {
        try {
            resolvesGenericSuperclassViaClassAndTypeReferenceVpack();
        } finally {
        }
    }


    void __invoke_specificWildcardFieldTypeUnchangedVpack() throws Exception {
        try {
            specificWildcardFieldTypeUnchangedVpack();
        } finally {
        }
    }


    void __invoke_serializationPreservesPolymorphicTypeInfoVpack() throws Exception {
        try {
            serializationPreservesPolymorphicTypeInfoVpack();
        } finally {
        }
    }


    void __invoke_unboundedWildcardResolvesToObjectVpack() throws Exception {
        try {
            unboundedWildcardResolvesToObjectVpack();
        } finally {
        }
    }


    void __invoke_mutuallyRecursiveBoundsTypeReferenceVpack() throws Exception {
        try {
            mutuallyRecursiveBoundsTypeReferenceVpack();
        } finally {
        }
    }


    void __invoke_mutuallyRecursiveBoundsCollectionTypeVpack() throws Exception {
        try {
            mutuallyRecursiveBoundsCollectionTypeVpack();
        } finally {
        }
    }

}
