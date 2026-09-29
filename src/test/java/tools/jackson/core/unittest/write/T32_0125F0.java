package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.core.util.JsonGeneratorDelegate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0125F0 {

    void noCommentSupportForVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            assertFalse(generator.canWriteComments());
            UnsupportedOperationException exception = assertThrows(
                    UnsupportedOperationException.class,
                    () -> generator.writeComment("a comment"));
            assertMessageContains(exception, "does not support writing Comments");
        }
    }

    void noCommentSupportNullCommentForVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            UnsupportedOperationException exception = assertThrows(
                    UnsupportedOperationException.class,
                    () -> generator.writeComment(null));
            assertMessageContains(exception, "does not support writing Comments");
        }
    }

    void delegateWriteCommentForVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator underlying = generator(output)) {
            JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(underlying);
            try {
                UnsupportedOperationException exception = assertThrows(
                        UnsupportedOperationException.class,
                        () -> delegate.writeComment("test"));
                assertMessageContains(exception, "does not support writing Comments");
            } finally {
                delegate.close();
            }
        }
    }

    void delegateWriteCommentReturnValue() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator underlying = generator(output)) {
            JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(underlying) {
                @Override
                public JsonGenerator writeComment(String comment) {
                    return this;
                }
            };
            assertSame(delegate, delegate.writeComment("test"));
            delegate.close();
        }
    }
private static byte[] writeObjectTypeId(WritableTypeId.Inclusion inclusion,
            String asProperty, boolean parentProperty) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            WritableTypeId typeId = new WritableTypeId(new Object(), JsonToken.START_OBJECT,
                    "typeId");
            typeId.include = inclusion;
            typeId.asProperty = asProperty;
            if (parentProperty) {
                generator.writeStartObject();
                generator.writeName("value");
            }
            generator.writeTypePrefix(typeId);
            generator.writeNumberProperty(parentProperty ? "number" : "value",
                    parentProperty ? 42 : 13);
            generator.writeTypeSuffix(typeId);
            if (parentProperty) {
                generator.writeEndObject();
            }
        }
        return output.toByteArray();
    }
private static byte[] writeArrayTypeId(WritableTypeId.Inclusion inclusion) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            WritableTypeId typeId = new WritableTypeId(new Object(), JsonToken.START_ARRAY,
                    "typeId");
            typeId.include = inclusion;
            typeId.asProperty = "type";
            generator.writeTypePrefix(typeId);
            generator.writeNumber(13);
            generator.writeNumber(42);
            generator.writeTypeSuffix(typeId);
        }
        return output.toByteArray();
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }
private static void assertMessageContains(Exception exception, String expected) {
        String message = exception.getMessage();
        if (message == null || !message.contains(expected)) {
            throw new AssertionError("Expected message containing " + expected
                    + ", got: " + message, exception);
        }
    }

    void __invoke_noCommentSupportForVpack() throws Exception {
        try {
            noCommentSupportForVpack();
        } finally {
        }
    }


    void __invoke_noCommentSupportNullCommentForVpack() throws Exception {
        try {
            noCommentSupportNullCommentForVpack();
        } finally {
        }
    }


    void __invoke_delegateWriteCommentForVpack() throws Exception {
        try {
            delegateWriteCommentForVpack();
        } finally {
        }
    }


    void __invoke_delegateWriteCommentReturnValue() throws Exception {
        try {
            delegateWriteCommentReturnValue();
        } finally {
        }
    }

}
