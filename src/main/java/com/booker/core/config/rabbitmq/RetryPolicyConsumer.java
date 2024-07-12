package com.booker.core.config.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

@Slf4j
public abstract class RetryPolicyConsumer<T> {

    @Getter
    private final RabbitTemplate rabbitTemplate;

    @Getter
    private final ObjectMapper objectMapper;

    private final QueueProperties queueProperties;

    private final RetryRecoverSupportProducer retryRecoverSupportProducer;


    public RetryPolicyConsumer(RabbitTemplate rabbitTemplate,
                               QueueProperties queueProperties,
                               ObjectMapper objectMapper,
                               RetryRecoverSupportProducer retryRecoverSupportProducer) {

        this.rabbitTemplate = rabbitTemplate;
        this.queueProperties = queueProperties;
        this.objectMapper = objectMapper;
        this.retryRecoverSupportProducer = retryRecoverSupportProducer;
    }

    /**
     * 반드시 이 메소드를 @Override 하여 @RabbitListener 를 수신된 메시지를 super로 보내주길 바람.
     */
    public void handleMessage(Message message) throws Exception {
        int tryCount = getTryCount(message);

        try {
            doWork(message.getMessageProperties(), getMessage(message));
        } catch (AmqpRejectAndDontRequeueException e) {
            sendToDLQ(message, e);
            throw e;
        } catch (Exception e) {
            if (tryCount >= queueProperties.maxRetryCount()) {
                processSendToDLQ(message, e);
            } else {
                sendToRetry(message);
            }
        }
    }

    protected T getMessage(Message message) throws IOException {
        return objectMapper.readValue(message.getBody(), getGenericTypeClass());
    }

    /**
     * 현재 메시지의 tryCount
     * <p>
     * Header 에 태깅된 RETRY_COUNT 를 반환한다.
     * Header 에 정보가 없을 경우 최초 메시지로 판단하고 1을 반환한다.
     */
    protected int getTryCount(Message message) {
        return (int) message.getMessageProperties()
                .getHeaders()
                .getOrDefault(RetryRecoverSupportProducer.RETRY_COUNT_HEADER, 1);
    }

    /**
     * 메시지 재시도 (Requeue)
     * <p>
     * doWork 작업시 예외가 발생하면 재시도 정책에 따라서 재시도를 한다.
     * Header 에 재시도 카운트 정보를 갱신하여 다시 Requeue 한다.
     */
    protected void sendToRetry(Message message) {
        try {
            int retryCount = getTryCount(message) + 1;
            retryRecoverSupportProducer.retry(message, retryCount);
        } catch (Exception e) {
            log.error("#### sendToRetry fail : {}", e.getMessage());
        }
    }

    /**
     * 메시지 최종 실패 (DLQ)
     * <p>
     * 최종 재시도까지 실패하면 실패된 메시지의 공동묘지로 전송한다.
     */
    protected void sendToDLQ(Message message, Throwable e) {
        try {
            retryRecoverSupportProducer.recover(message, e);
        } catch (Exception err) {
            log.error("#### sendToDLQ fail : {}", err.getMessage());
        }
    }

    /**
     * 메시지 실패 처리 Process
     * <p>
     * 1. beforeSendToDLQ 호출
     * 2. DLQ 전송
     * 3. afterSendToDLQ 호출
     * attention) before, after 에서 발생하는 예외는 씹음
     */
    protected void processSendToDLQ(Message message, Throwable e) {
        try {
            beforeSendToDLQ(message, e);
        } catch (Exception be) {
            log.error("## beforeSendToDlq error : {}", be.getMessage());
        }

        sendToDLQ(message, e);

        //monitoring.toSystemNotification(String.format("[ERROR] %s 확인요망", queueProperties.dlqRoutingKey()), e);

        try {
            afterSendToDLQ(message, e);
        } catch (Exception ae) {
            log.error("## afterSendToDlq error : {}", ae.getMessage());
        }

        throw new AmqpRejectAndDontRequeueException("limit max retry");
    }

    protected abstract Class<T> getGenericTypeClass();

    /**
     * 수신된 메시지에 대한 작업을 수행한다.
     * <p>
     * Exception 발생하면 정책에 따라 재시도 한다.
     * <p>
     * retry 원하지 않을 경우 AmqpRejectAndDontRequeueException 던지면 바로 DLQ 로 직배송
     */
    protected abstract void doWork(MessageProperties messageProperties, T message) throws Exception;

    /**
     * 메시지 최종 실패 처리 전에 호출됩니다.
     */
    protected abstract void beforeSendToDLQ(Message message, Throwable e) throws Exception;

    /**
     * 메시지 최종 실패 처리 후에 호출됩니다.
     */
    protected abstract void afterSendToDLQ(Message message, Throwable e) throws Exception;
}
