package com.roy.examples;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class EventHeaders {

    Logger logger = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);

    public static final String ATTEMPTS = "attempts";
    private final Headers headers;

    public EventHeaders(Headers headers){
        this.headers = headers;
    }

    public void  incrementAttemptCounter() {
        final Optional<Header> statusHeader = Arrays.stream(headers.toArray())
                .filter(h -> h.key().contains(ATTEMPTS)).findFirst();
        if(statusHeader.isPresent()){
            final Header header = statusHeader.get();
            final String existingKey = header.key();
            logger.info("Header key " + existingKey);
            final byte[] existingValue = header.value();
            final String stringValue = new String(existingValue);
            final int count = Integer.parseInt(stringValue);
            logger.info("Header value " + count);
            final String newHeaderValue = String.valueOf(count + 1);
            headers.remove(ATTEMPTS);
            headers.add(ATTEMPTS,newHeaderValue.getBytes());
        }
        else{
            headers.add(ATTEMPTS,"1".getBytes());
        }
    }
}
