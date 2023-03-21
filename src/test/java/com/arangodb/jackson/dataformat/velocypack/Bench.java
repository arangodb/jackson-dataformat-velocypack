package com.arangodb.jackson.dataformat.velocypack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 8, time = 1)
@Measurement(iterations = 30, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
public class Bench {
    @State(Scope.Benchmark)
    public static class Data {

        public final byte[] vpack;
        public final byte[] json;

        public final JsonNode jsonNode;
        public final JsonNode vpackNode;

        public final ObjectMapper jsonMapper = new ObjectMapper();
        public final ObjectMapper vpackMapper = new VPackMapper();

        public Data() {
            try {
                String str = new String(Files.readAllBytes(
                        Paths.get(Bench.class.getResource("/api-docs.json").toURI())));
                JsonNode jn = jsonMapper.readTree(str);

                vpack = vpackMapper.writeValueAsBytes(jn);
                json = jsonMapper.writeValueAsBytes(jn);

                jsonNode = jsonMapper.readTree(json);
                vpackNode = vpackMapper.readTree(vpack);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        String datetime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        Path target = Files.createDirectories(Paths.get("target", "jmh-result"));

        ArrayList<String> jvmArgs = new ArrayList<>();
        jvmArgs.add("-Xms256m");
        jvmArgs.add("-Xmx256m");
        if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 11) {
            jvmArgs.add("-XX:StartFlightRecording=filename=" + target.resolve(datetime + ".jfr") + ",settings=profile");
        }

        Options opt = new OptionsBuilder()
                .include(Bench.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .jvmArgs(jvmArgs.toArray(new String[0]))
                .resultFormat(ResultFormatType.JSON)
                .result(target.resolve(datetime + ".json").toString())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void readTreeJson(Data data, Blackhole bh) throws IOException {
        readTree(data.json, bh, data.jsonMapper);
    }

    @Benchmark
    public void readTreeVPack(Data data, Blackhole bh) throws IOException {
        readTree(data.vpack, bh, data.vpackMapper);
    }

    private void readTree(byte[] bytes, Blackhole bh, ObjectMapper mapper) throws IOException {
        bh.consume(
                mapper.readTree(bytes)
        );
    }

    @Benchmark
    public void writeValueAsBytesJson(Data data, Blackhole bh) throws IOException {
        writeValueAsBytes(data.jsonNode, bh, data.jsonMapper);
    }

    @Benchmark
    public void writeValueAsBytesVPack(Data data, Blackhole bh) throws IOException {
        writeValueAsBytes(data.vpackNode, bh, data.vpackMapper);
    }

    private void writeValueAsBytes(JsonNode node, Blackhole bh, ObjectMapper mapper) throws IOException {
        bh.consume(
                mapper.writeValueAsBytes(node)
        );
    }

    @Benchmark
    public void roundTripJson(Data data, Blackhole bh) throws IOException {
        roundTrip(data.json, bh, data.jsonMapper);
    }

    @Benchmark
    public void roundTripVPack(Data data, Blackhole bh) throws IOException {
        roundTrip(data.vpack, bh, data.vpackMapper);
    }

    private void roundTrip(byte[] bytes, Blackhole bh, ObjectMapper mapper) throws IOException {
        bh.consume(
                mapper.writeValueAsBytes(
                        mapper.readTree(bytes)
                )
        );
    }

}
