package tools.jackson.databind.deser.bean;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonDeserializeAs;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.ArrayType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0188Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] ROOT_LIST = VPackWireFixtureTest.hex(
            "13 05 41 63 01");
private static final byte[] ARRAY_FIELDS = VPackWireFixtureTest.hex(
            "14 1c 46 6f 62 6a 41 72 72 13 05 20 11 01 "
          + "47 70 72 69 6d 41 72 72 13 05 20 11 01 02");
private static final byte[] PARAMETER_NAMES = VPackWireFixtureTest.hex(
            "14 1a 48 6f 70 65 6e 4e 61 6d 65 43 73 74 75 "
          + "47 6f 70 65 6e 41 67 65 20 16 02");

    void testModifierCalledTwice() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(arrayModifier(counter))
                .build();

        WrapperBean4216 input = mapper.readValue(ARRAY_FIELDS, WrapperBean4216.class);

        assertEquals(1, input.primArr.length);
        assertEquals((byte) 0x11, input.primArr[0]);
        assertEquals(1, input.objArr.length);
        assertEquals(Byte.valueOf((byte) 0x11), input.objArr[0]);
        assertEquals(2, counter.get());
    }
private static void runSuccess(VPackMapper mapper) throws Exception {
        Bean178 bean = mapper.readValue(PARAMETER_NAMES, Bean178.class);
        assertEquals("stu", bean.hiddenName());
        assertEquals(22, bean.hiddenAge());
    }
private static void runFailure(VPackMapper mapper) {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(PARAMETER_NAMES, NoNamesBean178.class));
        assertTrue(exception.getMessage().contains("Cannot construct instance of"));
        assertTrue(exception.getMessage().contains("no Creators, like default constructor, exist"));
    }
private static SimpleModule arrayModifier(AtomicInteger counter) {
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new ValueDeserializerModifier() {
            private static final long serialVersionUID = 1L;

            @Override
            public tools.jackson.databind.ValueDeserializer<?> modifyArrayDeserializer(
                    tools.jackson.databind.DeserializationConfig config,
                    ArrayType valueType,
                    tools.jackson.databind.BeanDescription.Supplier beanDescRef,
                    tools.jackson.databind.ValueDeserializer<?> deserializer) {
                counter.incrementAndGet();
                return deserializer;
            }
        });
        return module;
    }
static class RootStringImpl implements RootString {
        private final String contents;

        RootStringImpl(String contents) {
            this.contents = contents;
        }

        @Override
        public String contents() {
            return contents;
        }
    }
interface RootString {
        String contents();
    }
@JsonDeserialize(contentAs = RootStringImpl.class)
    static class RootList extends LinkedList<RootStringImpl> { }
@JsonDeserializeAs(content = RootStringImpl.class)
    static class RootList2 extends LinkedList<RootStringImpl> { }
record Bean178(String openName, int openAge) {
        String hiddenName() {
            return openName;
        }

        int hiddenAge() {
            return openAge;
        }
    }
static class NoNamesBean178 {
        NoNamesBean178(String openName, int openAge) { }
    }
static class WrapperBean4216 {
        public Byte[] objArr;
        public byte[] primArr;
    }

    void __invoke_testModifierCalledTwice() throws Exception {
        try {
            testModifierCalledTwice();
        } finally {
        }
    }

}
