package tools.jackson.databind.ext.jdk8;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PolymorphicOptionalTest
    extends DatabindTestUtil
{
    // For [datatype-jdk8#14]
    public static class Container {
        public Optional<Contained> contained;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY)
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "ContainedImpl", value = ContainedImpl.class),
    })
    public static interface Contained { }

    public static class ContainedImpl implements Contained { }

    private final ObjectMapper MAPPER = newVPackMapper();

    // [datatype-jdk8#14]
    @Test
    public void testPolymorphic14() throws Exception
    {
        final Container dto = new Container();
        dto.contained = Optional.of(new ContainedImpl());

        final String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(dto));

        final Container fromJson = MAPPER.readValue(VPackUtils.toVPack(json), Container.class);
        assertNotNull(fromJson.contained);
        assertTrue(fromJson.contained.isPresent());
        assertSame(ContainedImpl.class, fromJson.contained.get().getClass());
    }
}
