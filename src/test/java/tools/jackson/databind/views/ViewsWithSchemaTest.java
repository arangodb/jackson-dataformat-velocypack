package tools.jackson.databind.views;

import org.junit.jupiter.api.Test;

/**
 * VPack compatibility delegates for:
 * {@link tools.jackson.databind.views.ViewsWithSchemaTest}
 */
class ViewsWithSchemaTest {
/**
 * VPack adaptation of {@link tools.jackson.databind.views.ViewsWithSchemaTest}.
 * Original test methods: {@link tools.jackson.databind.views.ViewsWithSchemaTest#testSchemaWithViews()}.
 */
    @Test
    // Provenance: ViewsWithSchemaTest#testSchemaWithViews().
    void schemaVisitorHonorsActiveViews() throws Exception {
            new T32_0651Fixture().__invoke_schemaVisitorHonorsActiveViews();
        }
/**
 * VPack adaptation of {@link tools.jackson.databind.views.ViewsWithSchemaTest}.
 * Original test methods: {@link tools.jackson.databind.views.ViewsWithSchemaTest#testSchemaWithoutViews()}.
 */
    @Test
    // Provenance: ViewsWithSchemaTest#testSchemaWithoutViews().
    void schemaVisitorWithoutViewListsAllProperties() throws Exception {
            new T32_0651Fixture().__invoke_schemaVisitorWithoutViewListsAllProperties();
        }
}
