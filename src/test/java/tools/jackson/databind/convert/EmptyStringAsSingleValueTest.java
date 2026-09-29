package tools.jackson.databind.convert;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

// [databind#3418]: Coercion from empty String to Collection<String>, with
// `DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY`
public class EmptyStringAsSingleValueTest
{
    static final class StringWrapper {
        private final String s;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public StringWrapper(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return "StringWrapper{" + s + "}";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StringWrapper && ((StringWrapper) obj).s.equals(s);
        }

        @Override
        public int hashCode() {
            return s.hashCode();
        }
    }

    private final ObjectMapper NORMAL_MAPPER = vpackMapperBuilder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();

    private final ObjectMapper COERCION_MAPPER = vpackMapperBuilder()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        // same as XmlMapper
            .withCoercionConfigDefaults(h -> {
                h.setAcceptBlankAsEmpty(true)
                    .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty);
            })
            .build();

    @Test
    public void testEmptyToList() throws Exception {
        // NO coercion + empty string input + StringCollectionDeserializer
        assertEquals(Collections.singletonList(""),
                NORMAL_MAPPER.readValue(VPackUtils.toVPack("\"\""), new TypeReference<List<String>>() {}));
    }

    @Test
    public void testEmptyToListWrapper() throws Exception {
        // NO coercion + empty string input + normal CollectionDeserializer
        assertEquals(Collections.singletonList(new StringWrapper("")),
                NORMAL_MAPPER.readValue(VPackUtils.toVPack("\"\""), new TypeReference<List<StringWrapper>>() {}));
    }

    @Test
    public void testCoercedEmptyToList() throws Exception {
        // YES coercion + empty string input + StringCollectionDeserializer
        assertEquals(Collections.emptyList(), COERCION_MAPPER.readValue(VPackUtils.toVPack("\"\""),
                new TypeReference<List<String>>() {}));
    }

    @Test
    public void testCoercedEmptyToListWrapper() throws Exception {
        // YES coercion + empty string input + normal CollectionDeserializer
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(VPackUtils.toVPack("\"\""), new TypeReference<List<StringWrapper>>() {}));
    }

    @Test
    public void testCoercedListToList() throws Exception {
        // YES coercion + empty LIST input + StringCollectionDeserializer
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(VPackUtils.toVPack("[]"), new TypeReference<List<String>>() {}));
    }

    @Test
    public void testCoercedListToListWrapper() throws Exception {
        // YES coercion + empty LIST input + normal CollectionDeserializer
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(VPackUtils.toVPack("[]"), new TypeReference<List<StringWrapper>>() {}));
    }

    @Test
    public void testBlankToList() throws Exception {
        // NO coercion + empty string input + StringCollectionDeserializer
        assertEquals(Collections.singletonList(" "),
                NORMAL_MAPPER.readValue(VPackUtils.toVPack("\" \""), new TypeReference<List<String>>() {}));
    }

    @Test
    public void testBlankToListWrapper() throws Exception {
        // NO coercion + empty string input + normal CollectionDeserializer
        assertEquals(Collections.singletonList(new StringWrapper(" ")),
                NORMAL_MAPPER.readValue(VPackUtils.toVPack("\" \""), new TypeReference<List<StringWrapper>>() {}));
    }

    @Test
    public void testCoercedBlankToList() throws Exception {
        // YES coercion + empty string input + StringCollectionDeserializer
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(VPackUtils.toVPack("\" \""), new TypeReference<List<String>>() {}));
    }

    @Test
    public void testCoercedBlankToListWrapper() throws Exception {
        // YES coercion + empty string input + normal CollectionDeserializer
        assertEquals(Collections.emptyList(),
                COERCION_MAPPER.readValue(VPackUtils.toVPack("\" \""), new TypeReference<List<StringWrapper>>() {}));
    }

    @Test
    public void testEmptyToArray() throws Exception {
        // NO coercion + empty string input + StringCollectionDeserializer
        assertArrayEquals(new String[]{""},
                NORMAL_MAPPER.readValue(VPackUtils.toVPack("\"\""), new TypeReference<String[]>() {}));
    }

    @Test
    public void testEmptyToArrayWrapper() throws Exception {
        // NO coercion + empty string input + normal CollectionDeserializer
        assertArrayEquals(new StringWrapper[]{new StringWrapper("")},
                NORMAL_MAPPER.readValue(VPackUtils.toVPack("\"\""), new TypeReference<StringWrapper[]>() {}));
    }

    @Test
    public void testCoercedEmptyToArray() throws Exception {
        // YES coercion + empty string input + StringCollectionDeserializer
        assertArrayEquals(new String[0], COERCION_MAPPER.readValue(VPackUtils.toVPack("\"\""),
                new TypeReference<String[]>() {}));
    }

    @Test
    public void testCoercedEmptyToArrayWrapper() throws Exception {
        // YES coercion + empty string input + normal CollectionDeserializer
        assertArrayEquals(new StringWrapper[0],
                COERCION_MAPPER.readValue(VPackUtils.toVPack("\"\""), new TypeReference<StringWrapper[]>() {}));
    }

    @Test
    public void testCoercedListToArray() throws Exception {
        // YES coercion + empty LIST input + StringCollectionDeserializer
        assertArrayEquals(new String[0],
                COERCION_MAPPER.readValue(VPackUtils.toVPack("[]"), new TypeReference<String[]>() {}));
    }

    @Test
    public void testCoercedListToArrayWrapper() throws Exception {
        // YES coercion + empty LIST input + normal CollectionDeserializer
        assertArrayEquals(new StringWrapper[0],
                COERCION_MAPPER.readValue(VPackUtils.toVPack("[]"), new TypeReference<StringWrapper[]>() {}));
    }

    @Test
    public void testBlankToArray() throws Exception {
        // NO coercion + empty string input + StringCollectionDeserializer
        assertArrayEquals(new String[]{" "},
                NORMAL_MAPPER.readValue(VPackUtils.toVPack("\" \""), new TypeReference<String[]>() {}));
    }

    @Test
    public void testBlankToArrayWrapper() throws Exception {
        // NO coercion + empty string input + normal CollectionDeserializer
        assertArrayEquals(new StringWrapper[]{new StringWrapper(" ")},
                NORMAL_MAPPER.readValue(VPackUtils.toVPack("\" \""), new TypeReference<StringWrapper[]>() {}));
    }

    @Test
    public void testCoercedBlankToArray() throws Exception {
        // YES coercion + empty string input + StringCollectionDeserializer
        assertArrayEquals(new String[0],
                COERCION_MAPPER.readValue(VPackUtils.toVPack("\" \""), new TypeReference<String[]>() {}));
    }

    @Test
    public void testCoercedBlankToArrayWrapper() throws Exception {
        // YES coercion + empty string input + normal CollectionDeserializer
        assertArrayEquals(new StringWrapper[0],
                COERCION_MAPPER.readValue(VPackUtils.toVPack("\" \""), new TypeReference<StringWrapper[]>() {}));
    }
}
