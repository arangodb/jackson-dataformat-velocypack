package tools.jackson.databind.ext.jdk8;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalBasicTest
    extends DatabindTestUtil
{
    public static final class OptionalData {
        public Optional<String> myString;
    }

    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    public static final class OptionalGenericData<T> {
        Optional<T> myData;
    }

    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
    public static class Unit
    {
        public Optional<Unit> baseUnit;

		public Unit() {
		}

		public Unit(final Optional<Unit> u) {
			baseUnit = u;
		}

        public void link(final Unit u) {
            baseUnit = Optional.of(u);
        }
    }

    // To test handling of polymorphic value types

    public static class Container {
        public Optional<Contained> contained;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY)
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "ContainedImpl", value = ContainedImpl.class),
    })
    public static interface Contained { }

    public static class ContainedImpl implements Contained { }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testOptionalTypeResolution() throws Exception {
		// With 2.6, we need to recognize it as ReferenceType
		JavaType t = MAPPER.constructType(Optional.class);
		assertNotNull(t);
		assertEquals(Optional.class, t.getRawClass());
		assertTrue(t.isReferenceType());
	}

    @Test
    public void testDeserAbsent() throws Exception {
        Optional<?> value = MAPPER.readValue(VPackUtils.toVPack("null"),
                new TypeReference<Optional<String>>() {
        });
        assertFalse(value.isPresent());
    }

    @Test
    public void testDeserSimpleString() throws Exception {
		Optional<?> value = MAPPER.readValue(VPackUtils.toVPack("\"simpleString\""),
				new TypeReference<Optional<String>>() {
				});
		assertTrue(value.isPresent());
		assertEquals("simpleString", value.get());
    }

    @Test
    public void testDeserInsideObject() throws Exception {
		OptionalData data = MAPPER.readValue(VPackUtils.toVPack("{\"myString\":\"simpleString\"}"),
				OptionalData.class);
		assertTrue(data.myString.isPresent());
		assertEquals("simpleString", data.myString.get());
    }

    @Test
    public void testDeserComplexObject() throws Exception {
		TypeReference<Optional<OptionalData>> type = new TypeReference<Optional<OptionalData>>() {
		};
		Optional<OptionalData> data = MAPPER.readValue(
				VPackUtils.toVPack("{\"myString\":\"simpleString\"}"), type);
		assertTrue(data.isPresent());
		assertTrue(data.get().myString.isPresent());
		assertEquals("simpleString", data.get().myString.get());
    }

    @Test
	public void testDeserGeneric() throws Exception {
		TypeReference<Optional<OptionalGenericData<String>>> type = new TypeReference<Optional<OptionalGenericData<String>>>() {
		};
		Optional<OptionalGenericData<String>> data = MAPPER.readValue(
				VPackUtils.toVPack("{\"myData\":\"simpleString\"}"), type);
		assertTrue(data.isPresent());
		assertTrue(data.get().myData.isPresent());
		assertEquals("simpleString", data.get().myData.get());
	}

    @Test
	public void testSerAbsent() throws Exception {
		String value = VPackUtils.toJson(MAPPER.writeValueAsBytes(Optional.empty()));
		assertEquals("null", value);
	}

    @Test
	public void testSerSimpleString() throws Exception {
		String value = VPackUtils.toJson(MAPPER.writeValueAsBytes(Optional.of("simpleString")));
		assertEquals("\"simpleString\"", value);
	}

    @Test
	public void testSerInsideObject() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = Optional.of("simpleString");
		String value = VPackUtils.toJson(MAPPER.writeValueAsBytes(data));
		assertEquals("{\"myString\":\"simpleString\"}", value);
	}

    @Test
	public void testSerComplexObject() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = Optional.of("simpleString");
		String value = VPackUtils.toJson(MAPPER.writeValueAsBytes(Optional.of(data)));
		assertEquals("{\"myString\":\"simpleString\"}", value);
	}

    @Test
	public void testSerGeneric() throws Exception {
		OptionalGenericData<String> data = new OptionalGenericData<String>();
		data.myData = Optional.of("simpleString");
		String value = VPackUtils.toJson(MAPPER.writeValueAsBytes(Optional.of(data)));
		assertEquals("{\"myData\":\"simpleString\"}", value);
	}

    @Test
	public void testSerOptDefault() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = Optional.empty();
		String value = VPackUtils.toJson(vpackMapperBuilder().changeDefaultPropertyInclusion(
		        incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS))
		        .build()
		        .writeValueAsBytes(data));
		assertEquals("{\"myString\":null}", value);
	}

    @Test
	public void testSerOptNull() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = null;
		String value = VPackUtils.toJson(vpackMapperBuilder().changeDefaultPropertyInclusion(
		        incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
		        .build().writeValueAsBytes(data));
		assertEquals("{}", value);
	}

    @Test
	public void testSerOptNonEmpty() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = null;
		String value = VPackUtils.toJson(vpackMapperBuilder().changeDefaultPropertyInclusion(
		            incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
		        .build()
		        .writeValueAsBytes(data));
		assertEquals("{}", value);
	}

    @Test
	public void testWithTypingEnabled() throws Exception {
	    final ObjectMapper mapper = vpackMapperBuilder()
	            // ENABLE TYPING
	            .activateDefaultTyping(NoCheckSubTypeValidator.instance,
	                    DefaultTyping.OBJECT_AND_NON_CONCRETE)
	            .build();

		final OptionalData myData = new OptionalData();
		myData.myString = Optional.ofNullable("abc");

		final String json = VPackUtils.toJson(mapper.writeValueAsBytes(myData));
		final OptionalData deserializedMyData = mapper.readValue(VPackUtils.toVPack(json),
				OptionalData.class);
		assertEquals(myData.myString, deserializedMyData.myString);
	}

    @Test
	public void testObjectId() throws Exception {
		final Unit input = new Unit();
		input.link(input);
		String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));
		Unit result = MAPPER.readValue(VPackUtils.toVPack(json), Unit.class);
		assertNotNull(result);
		assertNotNull(result.baseUnit);
		assertTrue(result.baseUnit.isPresent());
		Unit base = result.baseUnit.get();
		assertSame(result, base);
	}

    @Test
	public void testOptionalCollection() throws Exception {

		TypeReference<List<Optional<String>>> typeReference = new TypeReference<List<Optional<String>>>() {
		};

		List<Optional<String>> list = new ArrayList<Optional<String>>();
		list.add(Optional.of("2014-1-22"));
		list.add(Optional.<String> empty());
		list.add(Optional.of("2014-1-23"));

		String str = VPackUtils.toJson(MAPPER.writeValueAsBytes(list));
		assertEquals("[\"2014-1-22\",null,\"2014-1-23\"]", str);

		List<Optional<String>> result = MAPPER.readValue(VPackUtils.toVPack(str), typeReference);
		assertEquals(list.size(), result.size());
		for (int i = 0; i < list.size(); ++i) {
			assertEquals(list.get(i), result.get(i), "Entry #" + i);
		}
	}

    @Test
	public void testPolymorphic() throws Exception
	{
	    final Container dto = new Container();
	    dto.contained = Optional.of((Contained) new ContainedImpl());

	    final String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(dto));

	    final Container fromJson = MAPPER.readValue(VPackUtils.toVPack(json), Container.class);
	    assertNotNull(fromJson.contained);
	    assertTrue(fromJson.contained.isPresent());
	    assertSame(ContainedImpl.class, fromJson.contained.get().getClass());
	}
}
