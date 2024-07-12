package com.booker.core.util;

import com.booker.core.constant.DateConst;
import org.springframework.amqp.core.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RabbitHeader {
    public static long calculateDelay(LocalDateTime receiveAt) {
        return calculateDelay(receiveAt, LocalDateTime.now());
    }

    public static long calculateDelay(LocalDateTime receiveAt, LocalDateTime base) {
        return Math.abs(ChronoUnit.MILLIS.between(receiveAt, base));
    }

    public static void setFailedAt(Message message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConst.LOCAL_DATE_TIME_DEFAULT_FORMAT);
        message.getMessageProperties().setHeader("x-failed-time", LocalDateTime.now().format(formatter));
    }
}