package com.roy.examples;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@SpringBootApplication
public class Application  {


    public static void main(String[] args) {
        final ConfigurableApplicationContext configurableApplicationContext
                = SpringApplication.run(Application.class, args);
        final EventPublisher eventPublisher
                = configurableApplicationContext.getBeanFactory().getBean("eventPublisher",EventPublisher.class);
        eventPublisher.publishMesssage("kycDocumentCreated", "kyc");
    }




    public void imageEncode() throws IOException {
        final File file = new File("/Users/sroy/Documents/Personal/sroy_pan.jpg");
        final byte[] src = FileUtils.readFileToByteArray(file);
        final String encodeToString = Base64.getEncoder().encodeToString(src);


        FileUtils.write(new File("encoded.txt"), encodeToString);
//        System.out.println(encodeToString);
    }
}
