package com.arangodb.jackson.dataformat.velocypack;

import jdk.jfr.*;
import org.openjdk.jmh.infra.*;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.results.*;
import org.openjdk.jmh.runner.IterationType;
import java.time.Instant;
import java.util.*;

/** Only loaded in profiling invocations; trial setup and warmup precede these intervals. */
public class MeasurementIntervals implements InternalProfiler {
    @Name("reader.MeasurementInterval")
    @Label("Reader measurement interval")
    @Enabled(true)
    @StackTrace(false)
    public static class Interval extends Event {
        public String start;
        public String end;
        public int iteration;
    }
    private Instant start;
    private int iteration;
    public String getDescription() { return "JFR measurement interval markers"; }
    public void beforeIteration(BenchmarkParams b, IterationParams i) {
        if (i.getType() == IterationType.MEASUREMENT) start = Instant.now();
    }
    public Collection<? extends Result> afterIteration(BenchmarkParams b, IterationParams i, IterationResult r) {
        if (i.getType() == IterationType.MEASUREMENT) {
            Interval event = new Interval();
            event.start = start.toString();
            event.end = Instant.now().toString();
            event.iteration = ++iteration;
            event.commit();
        }
        return Collections.emptyList();
    }
}
