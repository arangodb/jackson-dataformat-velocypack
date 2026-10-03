package com.arangodb.jackson.dataformat.velocypack;

import jdk.jfr.consumer.*;
import tools.jackson.databind.json.JsonMapper;
import java.nio.file.*;
import java.time.*;
import java.util.*;

/** Weight aggregation with complete recorded stacks, never allocation event-count attribution. */
public class ReaderJfrAnalysis {
    public static void main(String[] args) throws Exception {
        Path input = Path.of(args[0]);
        List<Map<String, Object>> intervals = new ArrayList<>();
        List<Instant[]> bounds = new ArrayList<>();
        try (RecordingFile file = new RecordingFile(input)) {
            while (file.hasMoreEvents()) {
                RecordedEvent e = file.readEvent();
                if (!e.getEventType().getName().equals("reader.MeasurementInterval")) continue;
                Instant start = Instant.parse(e.getString("start")), end = Instant.parse(e.getString("end"));
                bounds.add(new Instant[]{start, end});
                intervals.add(Map.of("start", start.toString(), "end", end.toString(), "iteration", e.getInt("iteration")));
            }
        }
        if (bounds.size() != 5) throw new IllegalStateException("Expected 5 measurement intervals; got " + bounds.size());
        // A sample's weight spans allocations since the previous sample on that thread.
        // The first sample inside each interval can include setup/warmup or interval gaps.
        // Find boundaries by timestamps (RecordingFile events need not be globally ordered).
        Map<String, Instant> firstSamples = new HashMap<>();
        try (RecordingFile file = new RecordingFile(input)) {
            while (file.hasMoreEvents()) {
                RecordedEvent e = file.readEvent();
                if (!e.getEventType().getName().equals("jdk.ObjectAllocationSample")) continue;
                int interval = interval(e.getStartTime(), bounds);
                if (interval < 0) continue;
                String key = interval + ":" + e.getThread().getId();
                firstSamples.merge(key, e.getStartTime(), (a,b) -> a.isBefore(b) ? a : b);
            }
        }
        Map<String, Long> classes = new TreeMap<>(), stacks = new TreeMap<>();
        List<Map<String, Object>> boundarySamples = new ArrayList<>();
        long totalWeight = 0, benchmarkWeight = 0, missingStackWeight = 0, truncatedWeight = 0, excludedWeight = 0;
        long boundaryWeight = 0;
        long includedEvents = 0;
        try (RecordingFile file = new RecordingFile(input)) {
            while (file.hasMoreEvents()) {
                RecordedEvent e = file.readEvent();
                if (!e.getEventType().getName().equals("jdk.ObjectAllocationSample")) continue;
                long weight = e.getLong("weight");
                Instant time = e.getStartTime();
                int interval = interval(time, bounds);
                if (interval < 0) { excludedWeight += weight; continue; }
                includedEvents++;
                boolean boundary = time.equals(firstSamples.get(interval + ":" + e.getThread().getId()));
                if (!boundary) totalWeight += weight;
                RecordedStackTrace trace = e.getStackTrace();
                if (trace == null) {
                    if (boundary) boundaryWeight += weight;
                    else missingStackWeight += weight;
                    continue;
                }
                StringBuilder stack = new StringBuilder();
                boolean benchmark = false;
                for (RecordedFrame f : trace.getFrames()) {
                    String name = f.getMethod().getType().getName()+"."+f.getMethod().getName();
                    stack.append(name).append(":").append(f.getLineNumber()).append(" [bci=").append(f.getBytecodeIndex()).append("]\n");
                    if (name.equals("com.arangodb.jackson.dataformat.velocypack.Bench.treeReadCursor")) benchmark = true;
                }
                if (trace.isTruncated() && !boundary) truncatedWeight += weight;
                String type = e.getClass("objectClass").getName();
                if (boundary) {
                    boundaryWeight += weight;
                    boundarySamples.add(Map.of("interval", interval+1, "thread", e.getThread().getJavaName(),
                            "time", time.toString(), "weight", weight, "class", type,
                            "stack", stack.toString(), "truncated", trace.isTruncated()));
                    continue;
                }
                if (!benchmark) continue;
                benchmarkWeight += weight;
                classes.merge(type, weight, Long::sum);
                stacks.merge(type+"\n"+stack, weight, Long::sum);
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recording", input.toString());
        result.put("measurementIntervals", intervals);
        result.put("measurementSeconds", bounds.stream().mapToDouble(b -> Duration.between(b[0],b[1]).toNanos()/1e9).sum());
        result.put("includedEventsForSamplingDiagnosticsOnly", includedEvents);
        result.put("intervalSampleWeightBytes", totalWeight);
        result.put("rawIntervalSampleWeightBytes", totalWeight + boundaryWeight);
        result.put("excludedBoundaryWeightBytes", boundaryWeight);
        result.put("excludedBoundarySamples", boundarySamples);
        result.put("treeReadCursorStackWeightBytes", benchmarkWeight);
        result.put("outsideIntervalWeightBytes", excludedWeight);
        result.put("missingStackWeightBytes", missingStackWeight);
        result.put("truncatedStackWeightBytes", truncatedWeight);
        result.put("allocationClassWeightBytes", classes);
        result.put("fullStackWeightBytes", stacks);
        result.put("interpretation", "Interior sample-weight estimates; first sample per thread/interval excluded because its weight can span setup/warmup/gaps. Conservative boundary exclusion also loses some measurement allocations. Not measured gc.alloc.rate.norm; no inference from raw event counts.");
        Files.write(Path.of(args[1]), JsonMapper.shared().writerWithDefaultPrettyPrinter().writeValueAsBytes(result));
    }
    static int interval(Instant time, List<Instant[]> bounds) {
        for (int i = 0; i < bounds.size(); i++) {
            Instant[] b = bounds.get(i);
            if (!time.isBefore(b[0]) && time.isBefore(b[1])) return i;
        }
        return -1;
    }
}
