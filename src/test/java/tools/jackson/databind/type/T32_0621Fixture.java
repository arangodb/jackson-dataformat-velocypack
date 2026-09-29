package tools.jackson.databind.type;

import java.lang.reflect.Type;

import tools.jackson.databind.JavaType;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0621Fixture {
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: TypeResolutionTest#wildcardResolvesToDeclaredBound().
    void wildcardResolvesToDeclaredBoundVpack() throws Exception {
        Type generic = WrapperHolder.class.getDeclaredField("wrapper").getGenericType();
        JavaType resolved = MAPPER.getTypeFactory().constructType(generic);
        assertEquals(MessageWrapper.class, resolved.getRawClass());
        assertEquals(1, resolved.containedTypeCount());
        assertEquals(Settings.class, resolved.containedType(0).getRawClass());
    }

    // Provenance: TypeResolutionTest#wildcardResolvesToNumberBound().
    void wildcardResolvesToNumberBoundVpack() throws Exception {
        Type generic = NumberBoxHolder.class.getDeclaredField("box").getGenericType();
        JavaType resolved = MAPPER.getTypeFactory().constructType(generic);
        assertEquals(NumberBox.class, resolved.getRawClass());
        assertEquals(1, resolved.containedTypeCount());
        assertEquals(Number.class, resolved.containedType(0).getRawClass());
        NumberBox<?> decoded = MAPPER.readerFor(resolved).readValue(
                MAPPER.writeValueAsBytes(new NumberBox<>(Integer.valueOf(7))));
        assertEquals(7, decoded.value.intValue());
    }
interface Settings { }
record MessageWrapper<T extends Settings>(T settings) { }
static class WrapperHolder { MessageWrapper<?> wrapper; }
static class NumberBox<T extends Number> { public T value; NumberBox() { } NumberBox(T value) { this.value = value; } }
static class NumberBoxHolder { NumberBox<?> box; }

    void __invoke_wildcardResolvesToDeclaredBoundVpack() throws Exception {
        try {
            wildcardResolvesToDeclaredBoundVpack();
        } finally {
        }
    }


    void __invoke_wildcardResolvesToNumberBoundVpack() throws Exception {
        try {
            wildcardResolvesToNumberBoundVpack();
        } finally {
        }
    }

}
