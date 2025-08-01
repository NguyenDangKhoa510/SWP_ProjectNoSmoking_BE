package org.datcheems.swp_projectnosmoking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to enable scheduling in the application.
 * This allows the use of @Scheduled annotations on methods.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // No additional configuration needed
}