package tools.jackson.core.unittest.util;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.type.ResolvedType;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.ByteArrayBuilder;
import tools.jackson.core.util.JsonRecyclerPools;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0105Fixture {

    void byteArrayBuilderSimpleContentIsPreserved() throws Exception {
        ByteArrayBuilder builder = new ByteArrayBuilder(null, 20);
        assertArrayEquals(new byte[0], builder.toByteArray());

        builder.write(0);
        builder.append(1);
        byte[] source = new byte[98];
        for (int i = 0; i < source.length; ++i) {
            source[i] = (byte) (2 + i);
        }
        builder.write(source);

        byte[] result = builder.toByteArray();
        assertEquals(100, result.length);
        for (int i = 0; i < 100; ++i) {
            assertEquals((byte) i, result[i]);
        }
        builder.release();
        builder.close();
    }

    void byteArrayBuilderAppendFourBytesWithPositiveValue() {
        ByteArrayBuilder builder = new ByteArrayBuilder(new BufferRecycler());
        assertEquals(0, builder.size());

        builder.appendFourBytes(2);

        assertEquals(4, builder.size());
        assertArrayEquals(new byte[] { 0, 0, 0, 2 }, builder.toByteArray());
        builder.close();
    }

    void byteArrayBuilderAppendTwoBytesWithZeroValue() {
        ByteArrayBuilder builder = new ByteArrayBuilder(0);
        assertEquals(0, builder.size());

        builder.appendTwoBytes(0);

        assertEquals(2, builder.size());
        assertArrayEquals(new byte[] { 0, 0 }, builder.toByteArray());
        builder.close();
    }

    void byteArrayBuilderFinishCurrentSegmentResetsCurrentLength() {
        ByteArrayBuilder builder = new ByteArrayBuilder(new BufferRecycler(), 2);
        builder.appendThreeBytes(2);

        assertEquals(3, builder.getCurrentSegmentLength());
        assertNotNull(builder.finishCurrentSegment());
        assertEquals(0, builder.getCurrentSegmentLength());
        builder.close();
    }

    void byteArrayBuilderRecyclerRemainsLinkedThroughVpackGeneratorClose() throws Exception {
        BufferRecycler recycler = new BufferRecycler()
                .withPool(JsonRecyclerPools.newBoundedPool(3));
        ByteArrayBuilder builder = new ByteArrayBuilder(recycler, 20);
        assertSame(recycler, builder.bufferRecycler());

        JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), builder);
        generator.writeStartArray();
        generator.writeEndArray();
        generator.close();

        assertTrue(recycler.isLinkedWithPool());
        assertArrayEquals(new byte[] { 0x01 }, builder.getClearAndRelease());
        assertTrue(recycler.isLinkedWithPool());
        recycler.releaseToPool();
        assertFalse(recycler.isLinkedWithPool());
    }
private static void assertMethod(Class<?> type, String name)
            throws NoSuchMethodException {
        assertNotNull(type.getDeclaredMethod(name));
    }
private static final class BogusResolvedType extends ResolvedType {
        private final boolean referenceType;

        BogusResolvedType(boolean referenceType) {
            this.referenceType = referenceType;
        }

        @Override public Class<?> getRawClass() { return null; }
        @Override public boolean hasRawClass(Class<?> clz) { return false; }
        @Override public boolean isAbstract() { return false; }
        @Override public boolean isConcrete() { return false; }
        @Override public boolean isThrowable() { return false; }
        @Override public boolean isArrayType() { return false; }
        @Override public boolean isEnumType() { return false; }
        @Override public boolean isInterface() { return false; }
        @Override public boolean isPrimitive() { return false; }
        @Override public boolean isFinal() { return false; }
        @Override public boolean isContainerType() { return false; }
        @Override public boolean isCollectionLikeType() { return false; }
        @Override public boolean isMapLikeType() { return false; }
        @Override public boolean hasGenericTypes() { return false; }
        @Override public ResolvedType getKeyType() { return null; }
        @Override public ResolvedType getContentType() { return null; }
        @Override public ResolvedType getReferencedType() {
            return referenceType ? this : null;
        }
        @Override public int containedTypeCount() { return 0; }
        @Override public ResolvedType containedType(int index) { return null; }
        @Override public String toCanonical() { return null; }
    }

    void __invoke_byteArrayBuilderSimpleContentIsPreserved() throws Exception {
        try {
            byteArrayBuilderSimpleContentIsPreserved();
        } finally {
        }
    }


    void __invoke_byteArrayBuilderAppendFourBytesWithPositiveValue() throws Exception {
        try {
            byteArrayBuilderAppendFourBytesWithPositiveValue();
        } finally {
        }
    }


    void __invoke_byteArrayBuilderAppendTwoBytesWithZeroValue() throws Exception {
        try {
            byteArrayBuilderAppendTwoBytesWithZeroValue();
        } finally {
        }
    }


    void __invoke_byteArrayBuilderFinishCurrentSegmentResetsCurrentLength() throws Exception {
        try {
            byteArrayBuilderFinishCurrentSegmentResetsCurrentLength();
        } finally {
        }
    }


    void __invoke_byteArrayBuilderRecyclerRemainsLinkedThroughVpackGeneratorClose() throws Exception {
        try {
            byteArrayBuilderRecyclerRemainsLinkedThroughVpackGeneratorClose();
        } finally {
        }
    }

}
