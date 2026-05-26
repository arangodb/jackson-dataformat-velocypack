package com.arangodb.jackson.dataformat.velocypack;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.VPackUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that serializing a bean with int[], long[], double[] fields as object
 * properties works correctly (regression for double _verifyValueWrite bug).
 */
public class PrimitiveArrayBeanTest {

    @JsonPropertyOrder({"ints", "longs", "doubles"})
    static class ArrayBean {
        public int[] ints;
        public long[] longs;
        public double[] doubles;

        public ArrayBean() {}

        public ArrayBean(int[] ints, long[] longs, double[] doubles) {
            this.ints = ints;
            this.longs = longs;
            this.doubles = doubles;
        }
    }

    private static final VPackMapper MAPPER = new VPackMapper();

    @Test
    public void testIntArrayBeanProperty() {
        ArrayBean bean = new ArrayBean(new int[]{1, 2, 3}, new long[0], new double[0]);
        byte[] bytes = MAPPER.writeValueAsBytes(bean);
        String json = VPackUtils.toJson(bytes);
        JsonNode node = MAPPER.readTree(bytes);
        assertThat(node.get("ints").isArray()).isTrue();
        assertThat(node.get("ints")).hasSize(3);
        assertThat(node.get("ints").get(0).intValue()).isEqualTo(1);
        assertThat(node.get("ints").get(1).intValue()).isEqualTo(2);
        assertThat(node.get("ints").get(2).intValue()).isEqualTo(3);
    }

    @Test
    public void testLongArrayBeanProperty() {
        ArrayBean bean = new ArrayBean(new int[0], new long[]{10L, 20L, 30L}, new double[0]);
        byte[] bytes = MAPPER.writeValueAsBytes(bean);
        JsonNode node = MAPPER.readTree(bytes);
        assertThat(node.get("longs").isArray()).isTrue();
        assertThat(node.get("longs")).hasSize(3);
        assertThat(node.get("longs").get(0).longValue()).isEqualTo(10L);
        assertThat(node.get("longs").get(1).longValue()).isEqualTo(20L);
        assertThat(node.get("longs").get(2).longValue()).isEqualTo(30L);
    }

    @Test
    public void testDoubleArrayBeanProperty() {
        ArrayBean bean = new ArrayBean(new int[0], new long[0], new double[]{1.1, 2.2, 3.3});
        byte[] bytes = MAPPER.writeValueAsBytes(bean);
        JsonNode node = MAPPER.readTree(bytes);
        assertThat(node.get("doubles").isArray()).isTrue();
        assertThat(node.get("doubles")).hasSize(3);
        assertThat(node.get("doubles").get(0).doubleValue()).isEqualTo(1.1);
        assertThat(node.get("doubles").get(1).doubleValue()).isEqualTo(2.2);
        assertThat(node.get("doubles").get(2).doubleValue()).isEqualTo(3.3);
    }

    @Test
    public void testAllArraysBeanRoundTrip() {
        ArrayBean bean = new ArrayBean(new int[]{-1, 0, 100}, new long[]{Long.MIN_VALUE, 0L, Long.MAX_VALUE}, new double[]{-1.5, 0.0, 1.5});
        byte[] bytes = MAPPER.writeValueAsBytes(bean);
        String json = VPackUtils.toJson(bytes);
        assertThat(json).contains("\"ints\":[-1,0,100]");
        assertThat(json).contains("\"doubles\":[-1.5,0.0,1.5]");

        ArrayBean result = MAPPER.readValue(bytes, ArrayBean.class);
        assertThat(result.ints).containsExactly(-1, 0, 100);
        assertThat(result.longs).containsExactly(Long.MIN_VALUE, 0L, Long.MAX_VALUE);
        assertThat(result.doubles).containsExactly(-1.5, 0.0, 1.5);
    }
}
