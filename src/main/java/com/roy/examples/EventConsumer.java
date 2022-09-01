package com.roy.examples;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.nio.charset.StandardCharsets;
import java.util.Date;


@Configuration
@EnableKafka
public class EventConsumer {

    public static final String EXTRACTION = "Extraction";
    public static final String STATE = "state";
    Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    final
    ExternalService externalService;

    @Autowired
    public EventConsumer(ExternalService externalService) {
        this.externalService = externalService;
    }

//    @RetryableTopic(attempts = "2")
    @KafkaListener(topics = "kyc", groupId = "kyc-listener" ,
            containerFactory = "kafkaListenerContainerFactoryWithExpBackOff")
    void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        final String value = data.value();
        final String topic = data.topic();
        final String info = String.format("Processing message %s from topic %s on %s"
                , value, topic, new Date());
        logger.info(info);
        System.out.println(info);
//        incrementAttemptCounter(data);
        data.headers().add(STATE, EXTRACTION.getBytes(StandardCharsets.UTF_8));
        externalService.execute(value);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = {"kyc.DLT", "kyc-dlt"}, groupId = "kyc-dlt-listener")
    void onDlt(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        final String topic = data.topic();
        final String value = data.value();
        final String info = String.format("DLT topic : %s message %s", topic, value);
        for(Header h : data.headers().headers(STATE)){
            final String headerValue = new String(h.value(), StandardCharsets.UTF_8);
            logger.info("Header value in DLT is " + headerValue);
        }
        logger.info(info);
        acknowledgment.acknowledge();
    }

    private void incrementAttemptCounter(ConsumerRecord<String, String> data) {
        final Headers headers = data.headers();
        new EventHeaders(headers).incrementAttemptCounter();
    }


}
