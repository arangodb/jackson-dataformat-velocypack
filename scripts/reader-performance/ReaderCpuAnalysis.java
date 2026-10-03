package com.arangodb.jackson.dataformat.velocypack;

import jdk.jfr.consumer.*;
import tools.jackson.databind.json.JsonMapper;
import java.nio.file.*;
import java.time.*;
import java.util.*;

/** Execution-sample stack attribution, separate from allocation weights and timing. */
public class ReaderCpuAnalysis {
    public static void main(String[] args) throws Exception {
        List<Instant[]> bounds = new ArrayList<>();
        try (RecordingFile file = new RecordingFile(Path.of(args[0]))) {
            while (file.hasMoreEvents()) {
                RecordedEvent e = file.readEvent();
                if (e.getEventType().getName().equals("reader.MeasurementInterval")) {
                    bounds.add(new Instant[]{Instant.parse(e.getString("start")), Instant.parse(e.getString("end"))});
                }
            }
        }
        if (bounds.size() != 5) throw new IllegalStateException("Expected five measurement markers");
        Map<String, Long> inclusive = new TreeMap<>(), leaf = new TreeMap<>(), stacks = new TreeMap<>();
        long intervalSamples = 0, targetSamples = 0, missing = 0, truncated = 0;
        try (RecordingFile file = new RecordingFile(Path.of(args[0]))) {
            while (file.hasMoreEvents()) {
                RecordedEvent e = file.readEvent();
                if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
                Instant time = e.getStartTime();
                if (bounds.stream().noneMatch(b -> !time.isBefore(b[0]) && time.isBefore(b[1]))) continue;
                intervalSamples++;
                RecordedStackTrace trace = e.getStackTrace();
                if (trace == null) { missing++; continue; }
                if (trace.isTruncated()) truncated++;
                Set<String> methods = new HashSet<>();
                StringBuilder stack = new StringBuilder();
                String top = null;
                for (RecordedFrame f : trace.getFrames()) {
                    String name = f.getMethod().getType().getName() + "." + f.getMethod().getName();
                    if (top == null) top = name;
                    methods.add(name);
                    stack.append(name).append(":").append(f.getLineNumber()).append(" [bci=")
                            .append(f.getBytecodeIndex()).append("]\n");
                }
                if (!methods.contains("com.arangodb.jackson.dataformat.velocypack.Bench.treeReadCursor")) continue;
                targetSamples++;
                for (String method : methods) inclusive.merge(method, 1L, Long::sum);
                leaf.merge(top, 1L, Long::sum);
                stacks.merge(stack.toString(), 1L, Long::sum);
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recording", args[0]);
        result.put("measurementSeconds", bounds.stream().mapToDouble(b -> Duration.between(b[0], b[1]).toNanos()/1e9).sum());
        result.put("intervalExecutionSamples", intervalSamples);
        result.put("targetExecutionSamples", targetSamples);
        result.put("missingIntervalStacks", missing);
        result.put("truncatedIntervalStacks", truncated);
        result.put("inclusiveMethodSamples", inclusive);
        result.put("leafMethodSamples", leaf);
        result.put("fullStackSamples", stacks);
        result.put("interpretation", "Unweighted execution samples inside measurement markers. Inclusive methods overlap; samples and inlined source locations do not measure removable CPU time. No speedup inference without matched end-to-end trials.");
        Files.write(Path.of(args[1]), JsonMapper.shared().writerWithDefaultPrettyPrinter().writeValueAsBytes(result));
    }
}
