package com.booker.core.config.rabbitmq;

import org.springframework.amqp.core.Message;

public interface RetryRecoverSupportProducer {

    String RETRY_COUNT_HEADER = "RETRY_COUNT";

    void retry(Message message, int retryCount);

    void recover(Message message, Throwable e);
}
