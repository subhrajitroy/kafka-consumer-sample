package com.roy.examples;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EventConsumer {

    @KafkaListener(topics = "kyc",groupId = "kyc-listener")
    void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment){
        final String value = data.value();
        System.out.println("Message received " + value + " at " + new Date());
        if(value.contains("world")){
            if(acknowledgment != null)
            acknowledgment.acknowledge();
            return;
        }
        throw new RuntimeException("Message processing failed");
    }


}
