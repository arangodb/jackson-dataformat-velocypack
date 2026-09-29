package tools.jackson.databind.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0651Fixture {
private final ObjectMapper mapper = new VPackMapper();

    // Provenance: ViewsWithSchemaTest#testSchemaWithViews().
    void schemaVisitorHonorsActiveViews() {
        ListingVisitor visitor = new ListingVisitor();
        mapper.writerWithView(ViewBC.class).acceptJsonFormatVisitor(POJO.class, visitor);
        assertEquals(Arrays.asList("b", "c"), visitor.names);

        visitor = new ListingVisitor();
        mapper.writerWithView(ViewAB.class).acceptJsonFormatVisitor(POJO.class, visitor);
        assertEquals(Arrays.asList("a", "b"), visitor.names);
    }

    // Provenance: ViewsWithSchemaTest#testSchemaWithoutViews().
    void schemaVisitorWithoutViewListsAllProperties() {
        ListingVisitor visitor = new ListingVisitor();
        mapper.acceptJsonFormatVisitor(POJO.class, visitor);
        assertEquals(Arrays.asList("a", "b", "c"), visitor.names);
    }
interface ViewBC { }
interface ViewAB { }
@JsonPropertyOrder({ "a", "b", "c" })
    static class POJO {
        @JsonView({ ViewAB.class })
        public int a;

        @JsonView({ ViewAB.class, ViewBC.class })
        public int b;

        @JsonView({ ViewBC.class })
        public int c;
    }
static class ListingVisitor extends JsonFormatVisitorWrapper.Base {
        final List<String> names = new ArrayList<>();

        @Override
        public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
            return new JsonObjectFormatVisitor.Base(getContext()) {
                @Override
                public void optionalProperty(BeanProperty writer) {
                    names.add(writer.getName());
                }

                @Override
                public void optionalProperty(String name,
                        JsonFormatVisitable handler,
                        JavaType propertyTypeHint) {
                    names.add(name);
                }
            };
        }
    }

    void __invoke_schemaVisitorHonorsActiveViews() throws Exception {
        try {
            schemaVisitorHonorsActiveViews();
        } finally {
        }
    }


    void __invoke_schemaVisitorWithoutViewListsAllProperties() throws Exception {
        try {
            schemaVisitorWithoutViewListsAllProperties();
        } finally {
        }
    }

}
