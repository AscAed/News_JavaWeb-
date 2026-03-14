package com.zhouyi.component;

import com.zhouyi.service.NewsSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Startup runner to trigger Elasticsearch synchronization when the application starts.
 */
@Component
@Slf4j
public class EsSyncStartupRunner implements ApplicationRunner {

    @Autowired
    private NewsSyncService newsSyncService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application started. Triggering automatic Elasticsearch synchronization...");
        try {
            newsSyncService.fullSync();
        } catch (Exception e) {
            log.error("Automatic Elasticsearch synchronization failed during startup", e);
        }
    }
}
