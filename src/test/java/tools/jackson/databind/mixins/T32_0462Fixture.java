package tools.jackson.databind.mixins;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.util.StdConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0462Fixture {
private static final byte[] CONVERTER_INPUT = VPackWireFixtureTest.hex(
            "14 68 47 6c 6f 63 61 6c 65 73 13 19 "
          + "14 0b 44 63 6f 64 65 42 65 6e 01 14 0b 44 63 6f 64 65 42 64 65 01 02 "
          + "4e 6c 6f 63 61 6c 69 7a 65 64 54 65 78 74 73 13 35 "
          + "14 19 46 6c 6f 63 61 6c 65 42 65 6e 44 74 65 78 74 46 74 65 78 74 20 31 02 "
          + "14 19 46 6c 6f 63 61 6c 65 42 64 65 44 74 65 78 74 46 74 65 78 74 20 32 02 02 02");
private static final byte[] POLYMORPHIC_INPUT = VPackWireFixtureTest.hex(
            "0b 8d 01 45 72 6f 6f 6d 73 06 83 02 "
          + "0b 3d 03 44 74 79 70 65 46 4c 69 76 69 6e 67 43 74 79 70 46 4c 69 76 69 6e 67 "
          + "47 61 6e 69 6d 61 6c 73 02 18 "
          + "0b 16 02 44 74 79 70 65 43 43 61 74 43 74 79 70 43 43 61 74 0c 03 "
          + "1a 0f 03 "
          + "0b 41 03 44 74 79 70 65 48 53 6c 65 65 70 69 6e 67 43 74 79 70 48 53 6c 65 65 70 69 6e 67 "
          + "47 61 6e 69 6d 61 6c 73 02 18 "
          + "0b 16 02 44 74 79 70 65 43 44 6f 67 43 74 79 70 43 44 6f 67 0c 03 "
          + "1e 11 03 03 40 03");
private static final byte[] FULL_MODEL = VPackWireFixtureTest.hex(
            "0b 55 03 46 66 6f 72 6d 61 74 43 31 2e 30 45 63 68 69 6c 64 "
          + "0b 1d 02 44 74 79 70 65 47 43 48 49 4c 44 5f 42 44 6e 61 6d 65 45 74 65 73 74 42 10 03 "
          + "4a 6e 6f 74 56 69 73 69 62 6c 65 55 73 68 6f 75 6c 64 20 6e 6f 74 20 62 65 20 70 72 65 73 65 6e 74 "
          + "0e 03 31");
private static final byte[] VIEWED_MODEL = VPackWireFixtureTest.hex(
            "0b 25 02 46 66 6f 72 6d 61 74 43 31 2e 30 45 63 68 69 6c 64 "
          + "0b 0f 01 44 6e 61 6d 65 45 74 65 73 74 42 03 0e 03");

    // Provenance: MapperMixinsCopy1998Test#testSharedBuilder().
    void testSharedBuilderVpack() throws Exception {
        VPackMapper.Builder builder = VPackMapper.builder()
                .changeDefaultPropertyInclusion(inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_EMPTY));
        MyModelRoot input = new MyModelRoot();
        input.setChild(new MyChildB("testB"));

        ObjectMapper mapper = builder.build();
        assertEquals(mapper.readTree(FULL_MODEL), mapper.readTree(mapper.writeValueAsBytes(input)));

        mapper = builder
                .addMixIn(MyModelRoot.class, MixinConfig.MyModelRoot.class)
                .addMixIn(MyModelChildBase.class, MixinConfig.MyModelChildBase.class)
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();
        assertEquals(mapper.readTree(VIEWED_MODEL),
                mapper.readTree(mapper.writerWithView(MyModelView.class).writeValueAsBytes(input)));
    }
private static ObjectMapper converterMapper() {
        return VPackMapper.builder()
                .addMixIn(LocalizedText.class, LocalizedTextMixin.class)
                .addMixIn(Locale.class, LocaleMixin.class)
                .build();
    }
static class LocaleToStringConverter extends StdConverter<Locale, String> {
        @Override public String convert(Locale value) { return value.toString(); }
    }
static class StringToLocaleConverter extends StdConverter<String, Locale> {
        @Override public Locale convert(String value) { return Locale.forLanguageTag(value); }
    }
static class LocaleToJsonConverter extends StdConverter<Locale, Map<String, String>> {
        @Override public Map<String, String> convert(Locale value) {
            Map<String, String> result = new HashMap<>();
            result.put("code", value.toString());
            return result;
        }
    }
static class JsonToLocaleConverter extends StdConverter<Map<String, String>, Locale> {
        @Override public Locale convert(Map<String, String> value) {
            return Locale.forLanguageTag(value.get("code"));
        }
    }
@JsonDeserialize(converter = JsonToLocaleConverter.class)
    @JsonSerialize(converter = LocaleToJsonConverter.class)
    interface LocaleMixin { }
static class LocalizedText {
        private Locale locale;
        private String text;
        public Locale getLocale() { return locale; }
        public String getText() { return text; }
        public void setLocale(Locale value) { locale = value; }
        public void setText(String value) { text = value; }
    }
interface LocalizedTextMixin {
        @JsonSerialize(converter = LocaleToStringConverter.class)
        @JsonDeserialize(converter = StringToLocaleConverter.class)
        Locale getLocale();
    }
static class MyObject {
        private List<Locale> locales;
        private List<LocalizedText> localizedTexts;
        public List<Locale> getLocales() { return locales; }
        public List<LocalizedText> getLocalizedTexts() { return localizedTexts; }
        public void setLocales(List<Locale> value) { locales = value; }
        public void setLocalizedTexts(List<LocalizedText> value) { localizedTexts = value; }
    }
@JsonIgnoreProperties(value = { "type" }, allowSetters = true)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = LivingRoom.class, name = "Living"),
        @JsonSubTypes.Type(value = SleepingRoom.class, name = "Sleeping")
    })
    interface Room {
        @JsonProperty("typ") RoomType getTyp();
    }
record LivingRoom(@JsonProperty("typ") RoomType typ,
            @JsonProperty("animals") List<Cat> animals) implements Room {
        @Override public RoomType getTyp() { return typ; }
    }
record SleepingRoom(@JsonProperty("typ") RoomType typ,
            @JsonProperty("animals") List<Dog> animals) implements Room {
        @Override public RoomType getTyp() { return typ; }
    }
enum RoomType { Living, Sleeping }
@JsonIgnoreProperties(value = { "type" }, allowSetters = true)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Cat.class, name = "Cat"),
        @JsonSubTypes.Type(value = Dog.class, name = "Dog")
    })
    interface Animal {
        @JsonProperty("typ") AnimalType getTyp();
    }
record Cat(@JsonProperty("typ") AnimalType typ) implements Animal {
        @Override public AnimalType getTyp() { return typ; }
    }
record Dog(@JsonProperty("typ") AnimalType typ) implements Animal {
        @Override public AnimalType getTyp() { return typ; }
    }
enum AnimalType { Dog, Cat }
record Result(@JsonProperty("rooms") List<Room> rooms) { }
static class MyModelView { }
interface MixinConfig {
        interface MyModelRoot {
            @JsonView(MyModelView.class) String getFormat();
            @JsonView(MyModelView.class) MyModelChildBase getChild();
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.EXISTING_PROPERTY)
        interface MyModelChildBase {
            @JsonView(MyModelView.class) String getName();
        }
    }
@JsonPropertyOrder({ "format", "child" })
    static class MyModelRoot {
        @JsonProperty private String format = "1.0";
        @JsonProperty private MyModelChildBase child;
        @JsonProperty private String notVisible = "should not be present";
        public String getFormat() { return format; }
        public MyModelChildBase getChild() { return child; }
        public void setChild(MyModelChildBase value) { child = value; }
        public String getNotVisible() { return notVisible; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = MyChildA.class, name = "CHILD_A"),
        @JsonSubTypes.Type(value = MyChildB.class, name = "CHILD_B")
    })
    abstract static class MyModelChildBase {
        @JsonProperty private String name;
        public String getName() { return name; }
        @JsonIgnore public void setName(String value) { name = value; }
    }
static class MyChildA extends MyModelChildBase {
        MyChildA(String name) { setName(name); }
    }
static class MyChildB extends MyModelChildBase {
        MyChildB(String name) { setName(name); }
    }

    void __invoke_testSharedBuilderVpack() throws Exception {
        try {
            testSharedBuilderVpack();
        } finally {
        }
    }

}
