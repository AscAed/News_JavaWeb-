package com.zhouyi.component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers and exposes business-level metrics to Micrometer/Prometheus.
 * All counters and timers are pre-registered at construction time so they
 * appear in the /actuator/prometheus output even before the first event.
 */
@Component
public class NewsMetricsService {

    private final MeterRegistry meterRegistry;
    private final Counter articleViewCounter;
    private final Counter headlinePublishedCounter;
    private final Timer rssSyncTimer;

    // Role-tagged login counters are created on demand to avoid pre-defining roles
    private final ConcurrentHashMap<String, Counter> loginCounters = new ConcurrentHashMap<>();

    public NewsMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.articleViewCounter = Counter.builder("news_article_views_total")
                .description("Total number of article detail page views")
                .register(meterRegistry);

        this.headlinePublishedCounter = Counter.builder("news_headlines_published_total")
                .description("Total number of headlines published by editors")
                .register(meterRegistry);

        this.rssSyncTimer = Timer.builder("news_rss_sync_duration_seconds")
                .description("Time taken to complete a full RSS synchronization cycle")
                .register(meterRegistry);
    }

    /** Called each time an article detail is fetched. */
    public void incrementArticleView() {
        articleViewCounter.increment();
    }

    /** Called each time a new headline is published. */
    public void incrementHeadlinePublished() {
        headlinePublishedCounter.increment();
    }

    /**
     * Called on successful user login.
     *
     * @param role user role label, e.g. "ROLE_USER" or "ROLE_ADMIN"
     */
    public void incrementLogin(String role) {
        loginCounters.computeIfAbsent(role, r ->
            Counter.builder("news_user_login_total")
                    .description("Total successful logins by role")
                    .tag("role", r)
                    .register(meterRegistry)
        ).increment();
    }

    /**
     * Wraps an RSS sync operation in a Timer so its duration is recorded.
     *
     * @param syncTask the callable performing the RSS sync
     */
    public void recordRssSyncDuration(Runnable syncTask) {
        rssSyncTimer.record(syncTask);
    }
}
