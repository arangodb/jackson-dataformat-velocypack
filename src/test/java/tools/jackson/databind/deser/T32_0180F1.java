package tools.jackson.databind.deser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.util.TokenBuffer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0180F1 {
private static final byte[] NUMBER_BEAN = VPackWireFixtureTest.hex(
            "0b 0d 01 46 6e 75 6d 62 65 72 28 11 03");
private static final byte[] GENERIC_WRAPPER = VPackWireFixtureTest.hex(
            "0b 12 01 45 76 61 6c 75 65 0b 08 01 41 78 28 0d 03 03");
private static final byte[] GENERIC_WRAPPER_SINGLE_ARRAY = VPackWireFixtureTest.hex(
            "02 18 0b 16 01 45 76 61 6c 75 65 02 0c 0b 0a 01 41 78 02 04 28 0d 03 03");
private static final byte[] WRAPPER_BOOLEAN = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 1a 03");
private static final byte[] WRAPPER_STRING = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 61 62 63 03");
private static final byte[] WRAPPER_LONG = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 37 03");
private static final byte[] ARRAY_OF_WRAPPERS = VPackWireFixtureTest.hex(
            "02 13 0b 11 01 45 76 61 6c 75 65 0b 07 01 41 78 39 03 03");
private static final byte[] ARRAY_OF_WRAPPERS_SINGLE_ARRAY = VPackWireFixtureTest.hex(
            "02 17 0b 15 01 45 76 61 6c 75 65 02 0b 0b 09 01 41 78 02 03 39 03 03");
private static final byte[] RECURSIVE_TREE = VPackWireFixtureTest.hex(
            "02 05 02 03 01");
private static final byte[] RECURSIVE_ATTRIBUTES = VPackWireFixtureTest.hex(
            "0b 43 01 4a 61 74 74 72 69 62 75 74 65 73 02 34 0b 32 01 4a 61 74 74 72 69 62 75 74 65 73 02 23 0b 21 01 4a 61 74 74 72 69 62 75 74 65 73 02 12 0b 10 01 4a 61 74 74 72 69 62 75 74 65 73 18 03 03 03 03");
private static final byte[] PAIR_CONTAINER = VPackWireFixtureTest.hex(
            "0b 23 01 45 70 61 69 72 73 02 19 0b 17 02 44 6c 65 66 74 44 74 65 73 74 45 72 69 67 68 74 28 7b 03 0d 03");
private static final byte[] PAIR_BOTH_WILDCARDS = VPackWireFixtureTest.hex(
            "0b 19 02 44 6c 65 66 74 45 68 65 6c 6c 6f 45 72 69 67 68 74 29 c8 01 03 0e");

    void testDeeplyNestedArrays() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxNestingDepth(Integer.MAX_VALUE).build())
                .build();
        ObjectMapper mapper = VPackMapper.builder(factory).build();
        try (tools.jackson.core.JsonParser parser = mapper.createParser(deeplyNestedArrays(50_000))) {
            assertNotNull(parser.nextToken());
            TokenBuffer buffer = TokenBuffer.forGeneration();
            buffer.copyCurrentStructure(parser);
            buffer.close();
        }
    }
private static byte[] deeplyNestedArrays(int depth) {
        int[] lengths = new int[depth];
        int childLength = 2; // unsigned one-byte integer 123: 28 7b
        for (int i = 0; i < depth; ++i) {
            int length = 1 + 1 + childLength + 1;
            int width;
            do {
                width = forwardVarintWidth(length);
                int recalculated = 1 + width + childLength + 1;
                if (recalculated == length) {
                    break;
                }
                length = recalculated;
            } while (true);
            lengths[i] = length;
            childLength = length;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(childLength);
        for (int i = depth - 1; i >= 0; --i) {
            out.write(0x13);
            writeForwardVarint(out, lengths[i]);
        }
        out.write(0x28);
        out.write(0x7b);
        for (int i = 0; i < depth; ++i) {
            out.write(0x01);
        }
        return out.toByteArray();
    }
private static int forwardVarintWidth(int value) {
        int width = 1;
        while ((value >>>= 7) != 0) {
            ++width;
        }
        return width;
    }
private static void writeForwardVarint(ByteArrayOutputStream out, int value) {
        while (value >= 128) {
            out.write((value & 0x7f) | 0x80);
            value >>>= 7;
        }
        out.write(value);
    }
static class BaseNumberBean<T extends Number> {
        public void setNumber(T value) { }
    }
static class NumberBean extends BaseNumberBean<Long> {
        long _number;

        @Override
        public void setNumber(Long value) {
            _number = value.intValue();
        }
    }
static class SimpleBean {
        public int x;
    }
static class Wrapper<T> {
        public T value;
    }
static class Tree<T extends Tree<?>> {
        final List<T> children;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public Tree(List<T> children) {
            if (!children.stream().allMatch(c -> c instanceof Tree<?>)) {
                throw new IllegalArgumentException("Incorrect type");
            }
            this.children = children;
        }
    }
static class TestAttribute4118<T extends TestAttribute4118<?>> {
        public List<T> attributes;
    }
static class TestObject4118 {
        public List<TestAttribute4118<?>> attributes = new ArrayList<>();
    }
static class Pair<L, R> {
        public L left;
        public R right;
    }
static class PairContainer {
        public List<Pair<String, ?>> pairs;
    }

    void __invoke_testDeeplyNestedArrays() throws Exception {
        try {
            testDeeplyNestedArrays();
        } finally {
        }
    }

}
