package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PolymorphicViaRefTypeTest extends DatabindTestUtil
{
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "impl5", value = ImplForAtomic.class)
    })
    static class BaseForAtomic {
    }

    static class ImplForAtomic extends BaseForAtomic {
        public int x;

        protected ImplForAtomic() { }
        public ImplForAtomic(int x) { this.x = x; }
    }

    static class TypeInfoAtomic {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
        public AtomicReference<BaseForAtomic> value;
    }

    static class AtomicStringWrapper {
        public AtomicReference<StringWrapper> wrapper;

        protected AtomicStringWrapper() { }
        public AtomicStringWrapper(String str) {
            wrapper = new AtomicReference<StringWrapper>(new StringWrapper(str));
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testPolymorphicAtomicRefProperty() throws Exception
    {
        TypeInfoAtomic data = new TypeInfoAtomic();
        data.value = new AtomicReference<BaseForAtomic>(new ImplForAtomic(42));
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(data));
        TypeInfoAtomic result = MAPPER.readValue(VPackUtils.toVPack(json), TypeInfoAtomic.class);
        assertNotNull(result);
        BaseForAtomic value = result.value.get();
        assertNotNull(value);
        assertEquals(ImplForAtomic.class, value.getClass());
        assertEquals(42, ((ImplForAtomic) value).x);
    }

    @Test
    public void testAtomicRefViaDefaultTyping() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();
        AtomicStringWrapper data = new AtomicStringWrapper("foo");
        String json = VPackUtils.toJson(mapper.writer().writeValueAsBytes(data));
        AtomicStringWrapper result = mapper.readValue(VPackUtils.toVPack(json), AtomicStringWrapper.class);
        assertNotNull(result);
        assertNotNull(result.wrapper);
        assertEquals(AtomicReference.class, result.wrapper.getClass());
        StringWrapper w = result.wrapper.get();
        assertEquals("foo", w.str);
    }
}
