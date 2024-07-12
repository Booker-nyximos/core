package com.booker.core.config.rabbitmq;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookerRabbitTemplate extends RabbitTemplate {
    public BookerRabbitTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    /**
     * Producer 와 Consumer 사이에 Message type의 class가 달라서 생기는 문제를 해결하기 위해 Message 발송시에 __TypeId__ 헤더를 제거한다.
     */
    @Override
    public void convertAndSend(String exchange, String routingKey, Object object) throws AmqpException {
        super.convertAndSend(exchange, routingKey, object, message -> {
            message.getMessageProperties().getHeaders().remove("__TypeId__");
            return message;
        });
    }

    /**
     * Delay message 발송을 위한 Send
     */
    public void convertAndDelaySend(String exchange, String routingKey, long delay, Object object) throws AmqpException {
        this.convertAndSend(exchange, routingKey, object, message -> {
            removeTypeIdHeader(message);
            if (delay > 0) {
                message.getMessageProperties().getHeaders().put("x-delay", delay);
            }
            return message;
        });
    }

    private void removeTypeIdHeader(Message message) {
        message.getMessageProperties().getHeaders().remove("__TypeId__");
    }
}
