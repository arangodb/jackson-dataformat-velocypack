package tools.jackson.databind.node;

import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.exc.UnexpectedEndOfInputException;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.io.IOException;

public class TreeFromIncompleteVPackTest extends DatabindTestUtil
{
    final private ObjectMapper MAPPER = objectMapper(); // shared is fine

    @Test
    public void testErrorHandling() throws IOException {

      String json = "{\"A\":{\"B\":\n";
      JsonParser parser = MAPPER.createParser(VPackUtils.toVPack(json));
      try {
          parser.readValueAsTree();
      } catch (UnexpectedEndOfInputException e) {
          verifyException(e, "Unexpected end-of-input");
      }
      parser.close();

      try {
          MAPPER.readTree(VPackUtils.toVPack(json));
      } catch (UnexpectedEndOfInputException e) {
          verifyException(e, "Unexpected end-of-input");
      }

      try {
          MAPPER.reader().readTree(VPackUtils.toVPack(json));
      } catch (UnexpectedEndOfInputException e) {
          verifyException(e, "Unexpected end-of-input");
      }
    }
}
