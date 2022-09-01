package com.roy.examples;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component(value = "eventPublisher")
public class EventPublisher {



    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    public EventPublisher(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMesssage(String message, String topic) {
        final ListenableFuture<SendResult<String, String>> send
                = kafkaTemplate.send(topic, message);
        send.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Exception thrown when publishing message " + ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                final RecordMetadata recordMetadata = result.getRecordMetadata();
                System.out.println("Event published on topic " + recordMetadata.topic());
                System.out.println("Event published on partition " + recordMetadata.partition());
            }
        });
    }
}
