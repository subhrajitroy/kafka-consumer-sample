package com.roy.examples;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
public class EventConsumer {

    final
    ExternalService externalService;

    @Autowired
    public EventConsumer(ExternalService externalService) {
        this.externalService = externalService;
    }

    @KafkaListener(topics = "kyc",groupId = "kyc-listener")
    void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment){
        manageHeaders(data);

        externalService.execute(data.value());
        acknowledgment.acknowledge();
    }

    private void manageHeaders(ConsumerRecord<String, String> data) {
        final Headers headers = data.headers();
        final List<Header> headerList = Arrays.asList(headers.toArray());
        final Optional<Header> statusHeader = headerList.stream().filter(h -> h.key().contains("status")).findFirst();
        if(statusHeader.isPresent()){
            final Header header = statusHeader.get();
            final String key = header.key();
            System.out.println("Header key " + key);
            final byte[] value = header.value();
            final String stringValue = new String(value);
            final int count = Integer.parseInt(stringValue);
            System.out.println("Header value " + count);
            final String newHeaderValue = String.valueOf(count + 1);
            headers.remove("status");
            headers.add("status",newHeaderValue.getBytes());
        }
        else{
            headers.add("status","1".getBytes());
        }
    }


}
