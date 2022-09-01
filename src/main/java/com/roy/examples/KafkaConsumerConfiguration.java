package com.roy.examples;

import com.roy.examples.exceptions.FatalException;
import com.roy.examples.exceptions.TryAfterSometimeException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;

import static com.roy.examples.ApplicationConstants.BROKER;

/**
 * Any @Configuration annotated class on extending RetryConfigurationSupport will act as a source for beans which will be used
 * to handle @RetryableTopic annotated listeners. The KafkaListenerContainerFactory bean (if defined in such a class) does not seem
 * to have an effect.
 * The DefaultErrorHandler when set on KafkaListenerContainerFactory bean (in a @Configuration annotated class but not extending RetryConfigurationSupport
 *  or not annotated with @EnableKafkaRetryTopic) seem to be blocking retries as these do not use retry topics. Also in such case
 *  the DLT is {topicName}.DLT
 *  commitErrorHandler.setCommitRecovered(true) will ensure that recovered events are committed (requires AckMode.MANUAL_IMMEDIATE)
 *
 *
 */
//@EnableKafkaRetryTopic
//@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    Logger logger = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;


    @Bean(value = "kafkaListenerContainerFactoryWithExpBackOff")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,String>> kafkaListenerContainerFactory(){
        final ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(getConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);


        final ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2);
        backOff.setMaxElapsedTime(10000L);

        final DefaultErrorHandler commonErrorHandler
                = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate), backOff);
        commonErrorHandler.addNotRetryableExceptions(FatalException.class);
        commonErrorHandler.addNotRetryableExceptions(TryAfterSometimeException.class);
        /**
         * This property is set to true by default
         */
//        commonErrorHandler.setAckAfterHandle(true);
        commonErrorHandler.setCommitRecovered(true);
        factory.setCommonErrorHandler(commonErrorHandler);

        factory.setCommonErrorHandler(commonErrorHandler);
        return factory;
    }

//    @Bean
//    public RetryTopicConfiguration retryableTopic(KafkaTemplate<String, String> kafkaTemplate) {
//        return RetryTopicConfigurationBuilder
//                .newInstance()
//                .exponentialBackoff(2000,2,20000)
//                .includeTopics(singletonList(KYC_TOPICC_NAME))
//                .dltHandlerMethod(new EndpointHandlerMethod(DeadLetterTopicHandler.class,"process"))
//                .create(kafkaTemplate);
//    }

//    @Override
//    protected void configureBlockingRetries(RetryTopicConfigurationSupport.BlockingRetriesConfigurer blockingRetries) {
//        blockingRetries
//                .retryOn(BlockingRetryableException.class)
//                .backOff(new FixedBackOff(5000, 3));
//        //This will not use retry-topic, this will try to retry blocking(ly) and if @RetryTopic is used on listener
//        // then it will put the message there and when message is retried and if that fails that will also be retried in blocking
//        // manner (and may aging get retried async i.e via topic if @RetryTopic so configured
//    }
//
//    @Override
//    protected void manageNonBlockingFatalExceptions(List<Class<? extends Throwable>> nonBlockingFatalExceptions) {
//        nonBlockingFatalExceptions.add(FatalException.class);
//        nonBlockingFatalExceptions.add(NonBlockingRetryableException.class);
//    }

    /**
     * When extending {@link org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport} this bean is required
     * otherwise start up fails (internal backoffmanager is not created)
     * @return
     */
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
