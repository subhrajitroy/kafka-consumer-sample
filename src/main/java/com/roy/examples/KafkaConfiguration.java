package com.roy.examples;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.EndpointHandlerMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.roy.examples.ApplicationConstants.BROKER;
import static com.roy.examples.ApplicationConstants.KYC_TOPICC_NAME;
import static java.util.Collections.singletonList;

@Configuration
public class KafkaConfiguration {

    @Bean
    public KafkaAdmin getAdmin(){
        final HashMap<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        final KafkaAdmin kafkaAdmin = new KafkaAdmin(config);
        kafkaAdmin.setFatalIfBrokerNotAvailable(true);
        return kafkaAdmin;
    }

    @Bean
    public NewTopic getKYCTopic(){
        return TopicBuilder.name(KYC_TOPICC_NAME)
                .partitions(1).
                replicas(1)
                .build();
    }

    @Bean
    public RetryTopicConfiguration myRetryableTopic(KafkaTemplate<String, String> kafkaTemplate) {
        return RetryTopicConfigurationBuilder
                .newInstance()
                .exponentialBackoff(2000,2,20000)
                .includeTopics(singletonList(KYC_TOPICC_NAME))
                .dltHandlerMethod(new EndpointHandlerMethod(DeadLetterTopicHandler.class,"process"))
                .create(kafkaTemplate);
    }
}
