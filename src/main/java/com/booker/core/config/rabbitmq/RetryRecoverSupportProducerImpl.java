package com.booker.core.config.rabbitmq;


import com.booker.core.util.RabbitHeader;
import lombok.Getter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.LocalDateTime;

public class RetryRecoverSupportProducerImpl implements RetryRecoverSupportProducer {

    @Getter
    private RetryTemplate retryTemplate;

    @Getter
    private final RabbitTemplate rabbitTemplate;

    @Getter
    private final QueueProperties properties;

    @Getter
    private final RepublishMessageRecoverer messageRecoverer;

    public RetryRecoverSupportProducerImpl(RabbitTemplate rabbitTemplate, QueueProperties properties, RetryTemplate retryTemplate) {
        this(rabbitTemplate, properties);
        this.retryTemplate = retryTemplate == null ? defaultRetryTemplate() : retryTemplate;
    }

    public RetryRecoverSupportProducerImpl(RabbitTemplate rabbitTemplate, QueueProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;

        this.messageRecoverer = new RepublishMessageRecoverer(rabbitTemplate, properties.exchange(), properties.dlqRoutingKey());
        this.messageRecoverer.setErrorRoutingKeyPrefix("");
    }

    RetryTemplate defaultRetryTemplate() {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(100);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        return retryTemplate;
    }

    private int getBackoffSeconds(int retryCount) {
        return (int) Math.pow(2, retryCount);
    }

    @Override
    public void retry(Message message, int retryCount) {

        removeDelayHeader(message);

        this.retryTemplate.execute(context -> {
            message.getMessageProperties().getHeaders().put(RETRY_COUNT_HEADER, retryCount);
            int delay = getBackoffSeconds(retryCount);
            LocalDateTime receiveAt = LocalDateTime.now().plusSeconds(delay);
            ((BookerRabbitTemplate) getRabbitTemplate()).convertAndDelaySend(getProperties().exchange(), getProperties().originRoutingKey(), RabbitHeader.calculateDelay(receiveAt), message);
            return null;
        });
    }

    @Override
    public void recover(Message message, Throwable e) {

        removeDelayHeader(message);

        this.retryTemplate.execute(context -> {
            messageRecoverer.recover(message, e);
            return null;
        });
    }

    private void removeDelayHeader(Message message) {
        message.getMessageProperties().getHeaders().remove("x-delay");
    }
}
