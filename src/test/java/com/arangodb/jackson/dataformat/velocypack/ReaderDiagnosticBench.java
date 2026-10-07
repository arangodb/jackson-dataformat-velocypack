package com.arangodb.jackson.dataformat.velocypack;

import org.openjdk.jmh.annotations.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** Supplemental workloads. Never selected by the anchored cursor acceptance pattern. */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(3)
public class ReaderDiagnosticBench {
    @State(Scope.Thread)
    public static class Names {
        @Param({"JSON", "SMILE", "VPACK"}) public Bench.Format format;
        @Param({"repeated", "unique"}) public String naming;
        ObjectMapper mapper;
        byte[] bytes;

        @Setup public void setup() {
            mapper = format.newMapper();
            List<Map<String, Object>> values = new ArrayList<>();
            // Same pair count, name byte length, scalar payload and nesting in both cases.
            for (int i = 0; i < 1000; i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int j = 0; j < 16; j++) {
                    row.put(String.format("field%05d", naming.equals("unique") ? i * 16 + j : j), "value");
                }
                values.add(row);
            }
            bytes = mapper.writeValueAsBytes(values);
            if (!mapper.readTree(bytes).equals(mapper.valueToTree(values))) throw new IllegalStateException("name fixture");
            System.out.printf("names %s %s %d B%n", format, naming, bytes.length);
        }
    }

    @State(Scope.Thread)
    public static class Depth {
        @Param({"JSON", "SMILE", "VPACK"}) public Bench.Format format;
        @Param({"1", "4", "7", "16", "32"}) public int depth;
        ObjectMapper mapper;
        byte[] bytes;

        @Setup public void setup() {
            mapper = format.newMapper();
            Object value = "x".repeat(256 * 1024);
            for (int i = 0; i < depth; i++) value = List.of(value);
            bytes = mapper.writeValueAsBytes(value);
            if (!mapper.readTree(bytes).equals(mapper.valueToTree(value))) throw new IllegalStateException("depth fixture");
            System.out.printf("depth %s %d %d B%n", format, depth, bytes.length);
        }
    }

    @Benchmark public JsonNode names(Names state) { return state.mapper.readTree(state.bytes); }
    @Benchmark public JsonNode depth(Depth state) { return state.mapper.readTree(state.bytes); }
}
