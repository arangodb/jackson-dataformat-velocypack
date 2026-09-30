package com.arangodb.jackson.dataformat.velocypack;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

/**
 * Deterministic generator of ArangoDB-like documents.
 * <p>
 * Shapes mirror what applications typically store and fetch from ArangoDB:
 * system attributes ({@code _key}, {@code _id}, {@code _rev}), mixed scalars,
 * nested objects, GeoJSON, arrays of sub-documents, optional/null attributes,
 * non-ASCII text and occasional long strings (&gt; 126 UTF-8 bytes).
 */
public final class ArangoDocuments {

    public static final long SEED = 42L;
    private static final String COLLECTION = "customers";

    // ---------------------------------------------------------------- model

    public record Customer(
            @JsonProperty("_key") String key,
            @JsonProperty("_id") String id,
            @JsonProperty("_rev") String rev,
            String firstName,
            String lastName,
            String email,
            int age,
            boolean active,
            double balance,
            String createdAt,          // ISO-8601 string: the usual way dates are stored in ArangoDB
            long updatedAt,            // epoch millis: the other common convention
            String bio,                // nullable, sometimes long
            List<String> tags,
            Address address,
            Map<String, Object> preferences,
            List<Order> orders,
            String notes) {            // mostly null
    }

    public record Address(String street, String city, String zip, String country, GeoJson location) { }

    /** GeoJSON Point, as used by ArangoDB geo indexes: coordinates = [longitude, latitude]. */
    public record GeoJson(String type, List<Double> coordinates) { }

    public record Order(String orderId, String status, String currency, double total,
                        String placedAt, List<OrderItem> items) { }

    public record OrderItem(String sku, String name, int quantity, double unitPrice) { }

    /** Shape of an ArangoDB {@code POST /_api/cursor} response. */
    public record CursorResponse<T>(List<T> result, boolean hasMore, String id, int count,
                                    boolean cached, Map<String, Object> extra,
                                    boolean error, int code) { }

    // ---------------------------------------------------------------- dictionaries

    private record City(String name, String country, double lon, double lat) { }

    private static final String[] FIRST = {
            "Anna", "Luca", "Sofía", "Jürgen", "Łukasz", "Chloé", "Mateo", "Yuki", "Olga", "Noah", "Zoë", "Björn"};
    private static final String[] LAST = {
            "Müller", "Rossi", "García", "Kowalski", "Dubois", "Tanaka", "Ivanova", "Smith", "Nguyen", "Öztürk"};
    private static final City[] CITIES = {
            new City("Köln", "DE", 6.9603, 50.9375), new City("Milano", "IT", 9.1900, 45.4642),
            new City("Kraków", "PL", 19.9450, 50.0647), new City("São Paulo", "BR", -46.6333, -23.5505),
            new City("東京", "JP", 139.6917, 35.6895), new City("Zürich", "CH", 8.5417, 47.3769),
            new City("Lyon", "FR", 4.8357, 45.7640), new City("Austin", "US", -97.7431, 30.2672)};
    private static final String[] STREETS = {
            "Hauptstraße", "Via Roma", "ulica Floriańska", "Avenida Paulista", "Bahnhofstrasse",
            "Rue de la République", "Congress Avenue"};
    private static final String[] TAGS = {
            "premium", "newsletter", "beta", "b2b", "churn-risk", "vip", "eu", "apac"};
    private static final String[] STATUS = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "RETURNED"};
    private static final String[] CURRENCIES = {"EUR", "USD", "PLN", "JPY", "CHF"};
    private static final String[] PRODUCTS = {
            "Wireless Mouse", "USB-C Hub", "Mechanical Keyboard", "27\" Monitor", "Noise-Cancelling Headphones",
            "Laptop Stand", "Webcam 4K", "Ergonomic Chair", "Desk Lamp", "External SSD 1TB"};
    private static final String[] LANGS = {"en", "de", "it", "pl", "pt", "ja", "fr"};
    private static final String[] THEMES = {"light", "dark", "system"};
    private static final String[] WORDS = {
            "graph", "database", "multi-model", "query", "traversal", "index", "shard", "replication",
            "développeur", "Größe", "données", "análisis", "użytkownik", "データ", "cluster", "document"};
    private static final String REV_ALPHABET =
            "_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private ArangoDocuments() { }

    // ---------------------------------------------------------------- generation

    public static List<Customer> customers(int count, long seed) {
        SplittableRandom r = new SplittableRandom(seed);
        long base = Instant.parse("2023-01-01T00:00:00Z").toEpochMilli();
        List<Customer> out = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String key = Long.toString(1_000_000L + i * 7L + r.nextInt(7)); // unique, auto-key-like
            long created = base + r.nextLong(Duration.ofDays(900).toMillis());
            long updated = created + r.nextLong(Duration.ofDays(30).toMillis());
            out.add(new Customer(
                    key,
                    COLLECTION + "/" + key,
                    rev(r),
                    pick(r, FIRST),
                    pick(r, LAST),
                    "user" + key + "@example.com",
                    18 + r.nextInt(70),
                    r.nextInt(10) < 8,
                    round(r.nextDouble() * 10_000, 2),
                    Instant.ofEpochMilli(created).toString(),
                    updated,
                    r.nextInt(10) < 3 ? bio(r) : null,
                    tags(r),
                    address(r),
                    preferences(r),
                    orders(r, key, created),
                    r.nextInt(10) == 0 ? "manual review requested" : null));
        }
        return out;
    }

    public static CursorResponse<Customer> cursor(List<Customer> docs) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("writesExecuted", 0);
        stats.put("writesIgnored", 0);
        stats.put("scannedFull", docs.size());
        stats.put("scannedIndex", 0);
        stats.put("filtered", 0);
        stats.put("httpRequests", 0);
        stats.put("executionTime", 0.004217);
        stats.put("peakMemoryUsage", 131072);
        Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("warnings", List.of());
        extra.put("stats", stats);
        return new CursorResponse<>(docs, true, "2931445", docs.size(), false, extra, false, 201);
    }

    private static String rev(SplittableRandom r) {
        StringBuilder sb = new StringBuilder(11).append('_');
        for (int i = 0; i < 7; i++) sb.append(REV_ALPHABET.charAt(r.nextInt(REV_ALPHABET.length())));
        return sb.append("---").toString();
    }

    private static String bio(SplittableRandom r) {
        int words = 20 + r.nextInt(40);
        StringBuilder sb = new StringBuilder(words * 10);
        for (int i = 0; i < words; i++) {
            if (i > 0) sb.append(' ');
            sb.append(pick(r, WORDS));
        }
        return sb.append('.').toString();
    }

    private static List<String> tags(SplittableRandom r) {
        List<String> tags = new ArrayList<>(4);
        for (String t : TAGS) if (r.nextInt(4) == 0) tags.add(t);
        return tags;
    }

    private static Address address(SplittableRandom r) {
        City c = pick(r, CITIES);
        return new Address(
                (1 + r.nextInt(200)) + " " + pick(r, STREETS),
                c.name(),
                String.format("%05d", r.nextInt(100_000)),
                c.country(),
                new GeoJson("Point", List.of(
                        round(c.lon() + (r.nextDouble() - 0.5) * 0.2, 6),
                        round(c.lat() + (r.nextDouble() - 0.5) * 0.2, 6))));
    }

    private static Map<String, Object> preferences(SplittableRandom r) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("language", pick(r, LANGS));
        p.put("newsletter", r.nextBoolean());
        p.put("theme", pick(r, THEMES));
        p.put("itemsPerPage", (1 + r.nextInt(4)) * 25);
        return p;
    }

    private static List<Order> orders(SplittableRandom r, String key, long created) {
        int n = r.nextInt(5);
        List<Order> orders = new ArrayList<>(n);
        for (int j = 0; j < n; j++) {
            int itemCount = 1 + r.nextInt(5);
            List<OrderItem> items = new ArrayList<>(itemCount);
            double total = 0;
            for (int k = 0; k < itemCount; k++) {
                OrderItem item = new OrderItem(
                        String.format("SKU-%05d", r.nextInt(100_000)),
                        pick(r, PRODUCTS),
                        1 + r.nextInt(4),
                        round(1 + r.nextDouble() * 300, 2));
                total += item.quantity() * item.unitPrice();
                items.add(item);
            }
            orders.add(new Order(
                    key + "-" + j,
                    pick(r, STATUS),
                    pick(r, CURRENCIES),
                    round(total, 2),
                    Instant.ofEpochMilli(created + r.nextLong(Duration.ofDays(60).toMillis())).toString(),
                    items));
        }
        return orders;
    }

    private static <T> T pick(SplittableRandom r, T[] values) {
        return values[r.nextInt(values.length)];
    }

    private static double round(double v, int decimals) {
        double f = Math.pow(10, decimals);
        return Math.round(v * f) / f;
    }

    /** Prints a sample cursor response, handy for inspecting the payload. */
    public static void main(String[] args) {
        System.out.println(JsonMapper.shared().writerWithDefaultPrettyPrinter()
                .writeValueAsString(cursor(customers(3, SEED))));
    }
}
