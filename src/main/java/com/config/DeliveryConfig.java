package com.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "delivery")
public interface DeliveryConfig {

    /**
     * Specifies the maximum timeout for HTTP connections to the client side.
     */
    long clientTimeoutMs();

    /**
     * Determines how many times the application may retry if sending data to the client fails.
     */
    int retryMaxAttempts();
}
