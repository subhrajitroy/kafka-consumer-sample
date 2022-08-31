package com.roy.examples;

import com.roy.examples.exceptions.FatalException;
import com.roy.examples.exceptions.TryAfterSometimeException;
import com.roy.examples.exceptions.TryImmediatelyAgainException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.List;

import static com.roy.examples.ApplicationConstants.BROKER;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration extends RetryTopicConfigurationSupport {

    Logger logger = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;


//    @Bean
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,String>> kafkaListenerContainerFactory(){
//        final ConcurrentKafkaListenerContainerFactory<String, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(getConsumerFactory());
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//
//
////        final ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2);
////        backOff.setMaxElapsedTime(10000L);
////
////        final DefaultErrorHandler commonErrorHandler
////                = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate), backOff);
////
////        factory.setCommonErrorHandler(commonErrorHandler); final ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2);
////        backOff.setMaxElapsedTime(10000L);
////
////        final DefaultErrorHandler commonErrorHandler
////                = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate), backOff);
////
////        factory.setCommonErrorHandler(commonErrorHandler);
//        return factory;
//    }

//    @Bean
//    public RetryTopicConfiguration retryableTopic(KafkaTemplate<String, String> kafkaTemplate) {
//        return RetryTopicConfigurationBuilder
//                .newInstance()
//                .exponentialBackoff(2000,2,20000)
//                .includeTopics(singletonList(KYC_TOPICC_NAME))
//                .dltHandlerMethod(new EndpointHandlerMethod(DeadLetterTopicHandler.class,"process"))
//                .create(kafkaTemplate);
//    }

    @Override
    protected void configureBlockingRetries(RetryTopicConfigurationSupport.BlockingRetriesConfigurer blockingRetries) {
        blockingRetries
                .retryOn(TryAfterSometimeException.class)
                .backOff(new FixedBackOff(5000, 3));
        //This will not use retry-topic, this will try to retry blockingly and if @RetryTopic is used on listener
        // then it will put the message there and when message is retried and if that fails that will also be retried in blocking
        // manner (and may aging get retried async i.e via topic if @RetryTopic so configured
    }

    @Override
    protected void manageNonBlockingFatalExceptions(List<Class<? extends Throwable>> nonBlockingFatalExceptions) {
        nonBlockingFatalExceptions.add(FatalException.class);
        nonBlockingFatalExceptions.add(TryImmediatelyAgainException.class);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }



    private DefaultKafkaConsumerFactory<String, String> getConsumerFactory() {
        final HashMap<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configs);
    }

}
