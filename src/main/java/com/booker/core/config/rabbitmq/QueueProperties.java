package com.booker.core.config.rabbitmq;

public interface QueueProperties {

    /**
     * Exchange name
     */
    String exchange();

    /**
     * Origin message routing key
     */
    String originRoutingKey();

    /**
     * Dead letter queue routing key
     */
    String dlqRoutingKey();

    /**
     * Max retry count if work fail
     */
    int maxRetryCount();
}
