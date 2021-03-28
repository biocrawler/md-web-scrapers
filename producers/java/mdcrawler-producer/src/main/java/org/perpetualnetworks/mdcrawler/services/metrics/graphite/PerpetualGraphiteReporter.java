package org.perpetualnetworks.mdcrawler.services.metrics.graphite;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteSender;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerpetualGraphiteReporter extends ScheduledReporter {
    private final Map<String, Long> reportedCounters = new ConcurrentHashMap();
    private volatile double countFactor = 1.0D;
    private final GraphiteSender graphite;
    private final Clock clock;
    private final String prefix;

    public static PerpetualGraphiteReporter.Builder forRegistry(MetricRegistry registry) {
        return new PerpetualGraphiteReporter.Builder(registry);
    }

    protected PerpetualGraphiteReporter(MetricRegistry registry, GraphiteSender graphite, Clock clock, String prefix, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter, ScheduledExecutorService executor, boolean shutdownExecutorOnStop, Set<MetricAttribute> disabledMetricAttributes) {
        super(registry, "perpetual-graphite-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop, disabledMetricAttributes);
        this.graphite = graphite;
        this.clock = clock;
        this.prefix = prefix;
    }

    public void report(SortedMap<String, Gauge> gauges,
                       SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms,
                       SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {
        log.info("starting report with \n gauges: " + gauges + "\n counters: " + counters + "\n meters: " + meters
                + "\n timers: " + timers);

        long timestamp = this.clock.getTime() / 1000L;

        try {
            this.graphite.connect();
            Iterator var8 = gauges.entrySet().iterator();

            Entry entry;
            while(var8.hasNext()) {
                entry = (Entry)var8.next();
                this.reportGauge((String)entry.getKey(), (Gauge)entry.getValue(), timestamp);
            }

            var8 = counters.entrySet().iterator();

            while(var8.hasNext()) {
                entry = (Entry)var8.next();
                this.reportCounter((String)entry.getKey(), (Counter)entry.getValue(), timestamp);
            }

            var8 = histograms.entrySet().iterator();

            while(var8.hasNext()) {
                entry = (Entry)var8.next();
                this.reportHistogram((String)entry.getKey(), (Histogram)entry.getValue(), timestamp);
            }

            var8 = meters.entrySet().iterator();

            while(var8.hasNext()) {
                entry = (Entry)var8.next();
                log.info("reporting meter entry: " + entry.getKey() + " " + entry.getValue().toString());
                log.info("timestamp was: " + timestamp);
                this.reportMetered((String)entry.getKey(), (Metered)entry.getValue(), timestamp);
            }

            var8 = timers.entrySet().iterator();

            while(var8.hasNext()) {
                entry = (Entry)var8.next();
                log.info("reporting timer entry: " + entry.getKey() + " " + entry.getValue());
                this.reportTimer((String)entry.getKey(), (Timer)entry.getValue(), timestamp);
            }

            this.graphite.flush();
        } catch (IOException var18) {
            log.warn("Unable to report to Graphite", this.graphite, var18);
        } finally {
            try {
                this.graphite.close();
            } catch (IOException var17) {
                log.warn("Error closing Graphite", this.graphite, var17);
            }

        }

    }

    public void stop() {
        try {
            super.stop();
        } finally {
            try {
                this.graphite.close();
            } catch (IOException var7) {
                log.debug("Error disconnecting from Graphite", this.graphite, var7);
            }

        }

    }

    private void reportTimer(String name, Timer timer, long timestamp) throws IOException {
        Snapshot snapshot = timer.getSnapshot();
        this.sendIfEnabled(MetricAttribute.MAX, name, this.convertDuration((double)snapshot.getMax()), timestamp);
        this.sendIfEnabled(MetricAttribute.MEAN, name, this.convertDuration(snapshot.getMean()), timestamp);
        this.sendIfEnabled(MetricAttribute.MIN, name, this.convertDuration((double)snapshot.getMin()), timestamp);
        this.sendIfEnabled(MetricAttribute.STDDEV, name, this.convertDuration(snapshot.getStdDev()), timestamp);
        this.sendIfEnabled(MetricAttribute.P50, name, this.convertDuration(snapshot.getMedian()), timestamp);
        this.sendIfEnabled(MetricAttribute.P75, name, this.convertDuration(snapshot.get75thPercentile()), timestamp);
        this.sendIfEnabled(MetricAttribute.P95, name, this.convertDuration(snapshot.get95thPercentile()), timestamp);
        this.sendIfEnabled(MetricAttribute.P98, name, this.convertDuration(snapshot.get98thPercentile()), timestamp);
        this.sendIfEnabled(MetricAttribute.P99, name, this.convertDuration(snapshot.get99thPercentile()), timestamp);
        this.sendIfEnabled(MetricAttribute.P999, name, this.convertDuration(snapshot.get999thPercentile()), timestamp);
        this.reportMetered(name, timer, timestamp);
    }

    private void reportMetered(String name, Metered meter, long timestamp) throws IOException {
        if (!this.getDisabledMetricAttributes().contains(MetricAttribute.COUNT)) {
            this.reportCounter(name, meter.getCount(), timestamp);
        }

        this.sendIfEnabled(MetricAttribute.M1_RATE, name, this.convertRate(meter.getOneMinuteRate()), timestamp);
        this.sendIfEnabled(MetricAttribute.M5_RATE, name, this.convertRate(meter.getFiveMinuteRate()), timestamp);
        this.sendIfEnabled(MetricAttribute.M15_RATE, name, this.convertRate(meter.getFifteenMinuteRate()), timestamp);
        this.sendIfEnabled(MetricAttribute.MEAN_RATE, name, this.convertRate(meter.getMeanRate()), timestamp);
    }

    private void reportHistogram(String name, Histogram histogram, long timestamp) throws IOException {
        Snapshot snapshot = histogram.getSnapshot();
        if (!this.getDisabledMetricAttributes().contains(MetricAttribute.COUNT)) {
            this.reportCounter(name, histogram.getCount(), timestamp);
        }

        this.sendIfEnabled(MetricAttribute.MAX, name, snapshot.getMax(), timestamp);
        this.sendIfEnabled(MetricAttribute.MEAN, name, snapshot.getMean(), timestamp);
        this.sendIfEnabled(MetricAttribute.MIN, name, snapshot.getMin(), timestamp);
        this.sendIfEnabled(MetricAttribute.STDDEV, name, snapshot.getStdDev(), timestamp);
        this.sendIfEnabled(MetricAttribute.P50, name, snapshot.getMedian(), timestamp);
        this.sendIfEnabled(MetricAttribute.P75, name, snapshot.get75thPercentile(), timestamp);
        this.sendIfEnabled(MetricAttribute.P95, name, snapshot.get95thPercentile(), timestamp);
        this.sendIfEnabled(MetricAttribute.P98, name, snapshot.get98thPercentile(), timestamp);
        this.sendIfEnabled(MetricAttribute.P99, name, snapshot.get99thPercentile(), timestamp);
        this.sendIfEnabled(MetricAttribute.P999, name, snapshot.get999thPercentile(), timestamp);
    }

    private void sendIfEnabled(MetricAttribute type, String name, double value, long timestamp) throws IOException {
        if (!this.getDisabledMetricAttributes().contains(type)) {
            this.graphite.send(this.prefix(name, type.getCode()), this.format(value), timestamp);
        }
    }

    private void sendIfEnabled(MetricAttribute type, String name, long value, long timestamp) throws IOException {
        log.info("evaluating send if enabled with disabled attributes: " + this.getDisabledMetricAttributes());
        if (!this.getDisabledMetricAttributes().contains(type)) {
            final String prefix = this.prefix(name, type.getCode());
            final String format = this.format(value);
            log.info("sending data to prefix: " + prefix + " with value: " + format);
            this.graphite.send(prefix, format, timestamp);
        }
    }

    private void reportCounter(String name, Counter counter, long timestamp) throws IOException {
        this.reportCounter(name, counter.getCount(), timestamp);
    }

    private void reportCounter(String name, long value, long timestamp) throws IOException {
        this.graphite.send(this.prefix(name, MetricAttribute.COUNT.getCode()), this.format(value), timestamp);
        long diff = value - (Long)Optional.ofNullable((Long)this.reportedCounters.put(name, value)).orElse(0L);
        if (diff != 0L) {
            this.graphite.send(this.prefix(name, "hits"), this.format(diff), timestamp);
            this.graphite.send(this.prefix(name, "cps"), this.format((double)diff * this.countFactor), timestamp);
        }

    }

    private void reportGauge(String name, Gauge<?> gauge, long timestamp) throws IOException {
        String value = this.formatObject(gauge.getValue());
        if (value != null) {
            this.graphite.send(this.prefix(name), value, timestamp);
        }

    }

    private String formatObject(Object o) {
        if (o instanceof Float) {
            return this.format(((Float) o).doubleValue());
        } else if (o instanceof Double) {
            return this.format((Double) o);
        } else if (o instanceof Byte) {
            return this.format(((Byte)o).longValue());
        } else if (o instanceof Short) {
            return this.format(((Short)o).longValue());
        } else if (o instanceof Integer) {
            return this.format(((Integer)o).longValue());
        } else if (o instanceof Long) {
            return this.format((Double) o);
        } else if (o instanceof BigInteger) {
            return this.format(((BigInteger)o).doubleValue());
        } else if (o instanceof BigDecimal) {
            return this.format(((BigDecimal)o).doubleValue());
        } else {
            return o instanceof Boolean ? this.format((Boolean)o ? 1L : 0L) : null;
        }
    }

    private String prefix(String... components) {
        return MetricRegistry.name(this.prefix, components);
    }

    private String format(long n) {
        return Long.toString(n);
    }

    protected String format(double v) {
        return String.format(Locale.US, "%2.2f", v);
    }

    public void start(long period, TimeUnit unit) {
        this.countFactor = 1.0D / (double)unit.toMillis(period) * 1000.0D;
        super.start(period, unit);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private Clock clock;
        private String prefix;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private Set<MetricAttribute> disabledMetricAttributes;

        Builder(MetricRegistry registry) {
            this.registry = registry;
            this.clock = Clock.defaultClock();
            this.prefix = null;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.executor = null;
            this.shutdownExecutorOnStop = true;
            this.disabledMetricAttributes = Collections.emptySet();
        }

        public PerpetualGraphiteReporter.Builder shutdownExecutorOnStop(boolean shutdownExecutorOnStop) {
            this.shutdownExecutorOnStop = shutdownExecutorOnStop;
            return this;
        }

        public PerpetualGraphiteReporter.Builder scheduleOn(ScheduledExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public PerpetualGraphiteReporter.Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public PerpetualGraphiteReporter.Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public PerpetualGraphiteReporter.Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public PerpetualGraphiteReporter.Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public PerpetualGraphiteReporter.Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public PerpetualGraphiteReporter.Builder disabledMetricAttributes(Set<MetricAttribute> disabledMetricAttributes) {
            this.disabledMetricAttributes = disabledMetricAttributes;
            return this;
        }

        public PerpetualGraphiteReporter build(Graphite graphite) {
            return this.build((GraphiteSender)graphite);
        }

        public PerpetualGraphiteReporter build(GraphiteSender graphite) {
            return new PerpetualGraphiteReporter(this.registry, graphite, this.clock, this.prefix, this.rateUnit, this.durationUnit, this.filter, this.executor, this.shutdownExecutorOnStop, this.disabledMetricAttributes);
        }
    }
}
