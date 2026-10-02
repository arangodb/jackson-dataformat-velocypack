package com.arangodb.jackson.dataformat.velocypack;

import com.arangodb.jackson.dataformat.velocypack.ArangoDocuments.Address;
import com.arangodb.jackson.dataformat.velocypack.ArangoDocuments.Customer;
import com.arangodb.jackson.dataformat.velocypack.ArangoDocuments.CursorResponse;
import com.arangodb.jackson.dataformat.velocypack.ArangoDocuments.Order;
import com.arangodb.jackson.dataformat.velocypack.ArangoDocuments.OrderItem;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SequenceWriter;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.smile.SmileMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Serialization / deserialization benchmarks of Jackson JSON, Smile and VelocyPack
 * across the tree model, POJO databinding, streaming and sequence APIs.
 * <p>
 * Run all:          {@code Bench}
 * Run a subset:     {@code Bench "Bench\.pojo.*"}
 * Override params:  JMH {@code -p format=VPACK -p batchSize=100}
 */
@Warmup(iterations = 1, time = 2)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class Bench {

    public enum Format {
        JSON {
            @Override ObjectMapper newMapper() { return new JsonMapper(); }
        },
        SMILE {
            @Override ObjectMapper newMapper() { return new SmileMapper(); }
        },
        VPACK {
            @Override ObjectMapper newMapper() { return new VPackMapper(); }
        };

        abstract ObjectMapper newMapper();
    }

    @Param({"JSON", "SMILE", "VPACK"})
    public Format format;

    /** Documents per cursor batch / sequence; 1000 is ArangoDB's default cursor batch size. */
    @Param({"1000"})
    public int batchSize;

    private ObjectMapper mapper;
    private ObjectReader customerReader;
    private ObjectReader cursorReader;
    private ObjectWriter customerWriter;
    private ObjectWriter cursorWriter;

    // POJO inputs
    private Customer document;
    private List<Customer> documents;
    private CursorResponse<Customer> cursor;

    // tree inputs (decoded from each format's own bytes)
    private JsonNode documentTree;
    private JsonNode cursorTree;

    // encoded inputs
    private byte[] documentBytes;
    private byte[] cursorBytes;
    private byte[] sequenceBytes;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        mapper = format.newMapper();
        TypeReference<CursorResponse<Customer>> cursorType = new TypeReference<>() { };
        customerReader = mapper.readerFor(Customer.class);
        customerWriter = mapper.writerFor(Customer.class);
        cursorReader = mapper.readerFor(cursorType);
        cursorWriter = mapper.writerFor(cursorType);

        documents = ArangoDocuments.customers(batchSize, ArangoDocuments.SEED);
        document = documents.get(0);
        cursor = ArangoDocuments.cursor(documents);

        documentBytes = customerWriter.writeValueAsBytes(document);
        cursorBytes = cursorWriter.writeValueAsBytes(cursor);
        sequenceBytes = sequenceWrite();

        documentTree = mapper.readTree(documentBytes);
        cursorTree = mapper.readTree(cursorBytes);

        // Guard against a format silently producing different data.
        check(document.equals(customerReader.readValue(documentBytes)), "document round trip");
        check(cursor.equals(cursorReader.readValue(cursorBytes)), "cursor round trip");
        check(cursor.equals(cursorReader.readValue(streamingWriteCursor())), "streaming writer");
        check(cursor.equals(cursorReader.readValue(treeWriteCursor())), "tree writer");
        try (MappingIterator<Customer> it = customerReader.readValues(sequenceBytes)) {
            check(documents.equals(it.readAll()), "sequence round trip");
        }

        System.out.printf("%n[%s] document=%d B, cursor=%d B, sequence=%d B%n",
                format, documentBytes.length, cursorBytes.length, sequenceBytes.length);
    }

    public static void main(String[] args) throws RunnerException, IOException {
        String datetime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        Path target = Files.createDirectories(Paths.get("target", "jmh-result"));

        Options opt = new OptionsBuilder()
                .include(args.length > 0 ? args[0] : Bench.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .jvmArgs("-Xms512m", "-Xmx512m",
                        "-XX:StartFlightRecording=filename=" + target.resolve(datetime + ".jfr")
                                + ",settings=profile")
                .resultFormat(ResultFormatType.JSON)
                .result(target.resolve(datetime + ".json").toString())
                .build();

        new Runner(opt).run();
    }

    // ================================================================ tree model (JsonNode)

    @Benchmark
    public JsonNode treeReadDocument() {
        return mapper.readTree(documentBytes);
    }

    @Benchmark
    public byte[] treeWriteDocument() {
        return mapper.writeValueAsBytes(documentTree);
    }

    @Benchmark
    public JsonNode treeReadCursor() {
        return mapper.readTree(cursorBytes);
    }

    @Benchmark
    public byte[] treeWriteCursor() {
        return mapper.writeValueAsBytes(cursorTree);
    }

    // ================================================================ POJO databinding

    @Benchmark
    public Customer pojoReadDocument() {
        return customerReader.readValue(documentBytes);
    }

    @Benchmark
    public byte[] pojoWriteDocument() {
        return customerWriter.writeValueAsBytes(document);
    }

    @Benchmark
    public CursorResponse<Customer> pojoReadCursor() {
        return cursorReader.readValue(cursorBytes);
    }

    @Benchmark
    public byte[] pojoWriteCursor() {
        return cursorWriter.writeValueAsBytes(cursor);
    }

    // ================================================================ streaming (JsonParser / JsonGenerator)

    @Benchmark
    public void streamingReadDocument(Blackhole bh) {
        readTokens(documentBytes, bh);
    }

    @Benchmark
    public byte[] streamingWriteDocument() {
        return generate(documentBytes.length, g -> writeCustomer(g, document));
    }

    @Benchmark
    public void streamingReadCursor(Blackhole bh) {
        readTokens(cursorBytes, bh);
    }

    @Benchmark
    public byte[] streamingWriteCursor() {
        return generate(cursorBytes.length, g -> writeCursor(g, cursor));
    }

    // ================================================================ MappingIterator / SequenceWriter

    @Benchmark
    public void sequenceReadBytes(Blackhole bh) throws IOException {
        try (MappingIterator<Customer> it = customerReader.readValues(sequenceBytes)) {
            while (it.hasNextValue()) {
                bh.consume(it.nextValue());
            }
        }
    }

    /** InputStream source: exercises the owned/recycled page path of stream-based parsers. */
    @Benchmark
    public void sequenceReadInputStream(Blackhole bh) throws IOException {
        try (MappingIterator<Customer> it =
                     customerReader.readValues(new ByteArrayInputStream(sequenceBytes))) {
            while (it.hasNextValue()) {
                bh.consume(it.nextValue());
            }
        }
    }

    @Benchmark
    public byte[] sequenceWrite() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(
                sequenceBytes == null ? 1 << 16 : sequenceBytes.length);
        try (SequenceWriter w = customerWriter.writeValues(out)) {
            w.writeAll(documents);
        }
        return out.toByteArray();
    }

    // ================================================================ helpers

    private void readTokens(byte[] bytes, Blackhole bh) {
        try (JsonParser p = mapper.createParser(bytes)) {
            JsonToken t;
            while ((t = p.nextToken()) != null) {
                switch (t) {
                    case PROPERTY_NAME -> bh.consume(p.currentName());
                    case VALUE_STRING -> bh.consume(p.getString());
                    case VALUE_NUMBER_INT -> bh.consume(p.getLongValue());
                    case VALUE_NUMBER_FLOAT -> bh.consume(p.getDoubleValue());
                    case VALUE_TRUE, VALUE_FALSE -> bh.consume(p.getBooleanValue());
                    default -> bh.consume(t);
                }
            }
        }
    }

    private byte[] generate(int sizeHint, Consumer<JsonGenerator> body) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(sizeHint);
        try (JsonGenerator g = mapper.createGenerator(out)) {
            body.accept(g);
        }
        return out.toByteArray();
    }

    private static void writeCursor(JsonGenerator g, CursorResponse<Customer> c) {
        g.writeStartObject();
        g.writeArrayPropertyStart("result");
        for (Customer doc : c.result()) {
            writeCustomer(g, doc);
        }
        g.writeEndArray();
        g.writeBooleanProperty("hasMore", c.hasMore());
        g.writeStringProperty("id", c.id());
        g.writeNumberProperty("count", c.count());
        g.writeBooleanProperty("cached", c.cached());
        g.writeName("extra");
        writeAny(g, c.extra());
        g.writeBooleanProperty("error", c.error());
        g.writeNumberProperty("code", c.code());
        g.writeEndObject();
    }

    private static void writeCustomer(JsonGenerator g, Customer c) {
        g.writeStartObject();
        g.writeStringProperty("_key", c.key());
        g.writeStringProperty("_id", c.id());
        g.writeStringProperty("_rev", c.rev());
        g.writeStringProperty("firstName", c.firstName());
        g.writeStringProperty("lastName", c.lastName());
        g.writeStringProperty("email", c.email());
        g.writeNumberProperty("age", c.age());
        g.writeBooleanProperty("active", c.active());
        g.writeNumberProperty("balance", c.balance());
        g.writeStringProperty("createdAt", c.createdAt());
        g.writeNumberProperty("updatedAt", c.updatedAt());
        writeNullableString(g, "bio", c.bio());

        g.writeArrayPropertyStart("tags");
        for (String tag : c.tags()) {
            g.writeString(tag);
        }
        g.writeEndArray();

        Address a = c.address();
        g.writeObjectPropertyStart("address");
        g.writeStringProperty("street", a.street());
        g.writeStringProperty("city", a.city());
        g.writeStringProperty("zip", a.zip());
        g.writeStringProperty("country", a.country());
        g.writeObjectPropertyStart("location");
        g.writeStringProperty("type", a.location().type());
        g.writeArrayPropertyStart("coordinates");
        for (Double coordinate : a.location().coordinates()) {
            g.writeNumber(coordinate.doubleValue());
        }
        g.writeEndArray();
        g.writeEndObject();
        g.writeEndObject();

        g.writeName("preferences");
        writeAny(g, c.preferences());

        g.writeArrayPropertyStart("orders");
        for (Order o : c.orders()) {
            g.writeStartObject();
            g.writeStringProperty("orderId", o.orderId());
            g.writeStringProperty("status", o.status());
            g.writeStringProperty("currency", o.currency());
            g.writeNumberProperty("total", o.total());
            g.writeStringProperty("placedAt", o.placedAt());
            g.writeArrayPropertyStart("items");
            for (OrderItem i : o.items()) {
                g.writeStartObject();
                g.writeStringProperty("sku", i.sku());
                g.writeStringProperty("name", i.name());
                g.writeNumberProperty("quantity", i.quantity());
                g.writeNumberProperty("unitPrice", i.unitPrice());
                g.writeEndObject();
            }
            g.writeEndArray();
            g.writeEndObject();
        }
        g.writeEndArray();

        writeNullableString(g, "notes", c.notes());
        g.writeEndObject();
    }

    private static void writeNullableString(JsonGenerator g, String name, String value) {
        if (value == null) {
            g.writeNullProperty(name);
        } else {
            g.writeStringProperty(name, value);
        }
    }

    /** Writes untyped values (maps/lists/scalars) without going through databind. */
    private static void writeAny(JsonGenerator g, Object v) {
        if (v == null) {
            g.writeNull();
        } else if (v instanceof String s) {
            g.writeString(s);
        } else if (v instanceof Boolean b) {
            g.writeBoolean(b);
        } else if (v instanceof Integer n) {
            g.writeNumber(n.intValue());
        } else if (v instanceof Long n) {
            g.writeNumber(n.longValue());
        } else if (v instanceof Double n) {
            g.writeNumber(n.doubleValue());
        } else if (v instanceof Map<?, ?> m) {
            g.writeStartObject();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                g.writeName((String) e.getKey());
                writeAny(g, e.getValue());
            }
            g.writeEndObject();
        } else if (v instanceof List<?> l) {
            g.writeStartArray();
            for (Object o : l) {
                writeAny(g, o);
            }
            g.writeEndArray();
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + v.getClass());
        }
    }

    private static void check(boolean ok, String what) {
        if (!ok) {
            throw new IllegalStateException("Sanity check failed: " + what);
        }
    }
}
