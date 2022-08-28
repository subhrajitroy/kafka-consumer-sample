package com.roy.examples;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Component("dltHandler")
public class DeadLetterTopicHandler {

    public void process(ConsumerRecord<String, String> data, Acknowledgment acknowledgment){
        final String message = data.value();
        acknowledgment.acknowledge();
        System.out.println("Message received at DLT : " + message + " at " + new Date());
        final File file = new File("dlt.txt");
        try{
            FileUtils.writeStringToFile(file, message,true);
        }catch (IOException io){
            io.printStackTrace();
        }
    }
}
