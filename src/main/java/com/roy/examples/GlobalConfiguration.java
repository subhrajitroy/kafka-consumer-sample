//package com.roy.examples;
//
//import com.roy.examples.exceptions.FatalException;
//import com.roy.examples.exceptions.TryImmediatelyAgainException;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.util.backoff.FixedBackOff;
//
//import java.util.List;
//
//@EnableKafka
//@Configuration
//public class GlobalConfiguration extends RetryTopicConfigurationSupport {
//
//    @Override
//    protected void configureBlockingRetries(BlockingRetriesConfigurer blockingRetries) {
//        blockingRetries
//                .retryOn(TryImmediatelyAgainException.class)
//                .backOff(new FixedBackOff(3000, 1));
//    }
//
//
//    @Override
//    protected void manageNonBlockingFatalExceptions(List<Class<? extends Throwable>> nonBlockingFatalExceptions) {
//        nonBlockingFatalExceptions.add(FatalException.class);
//    }
//
//    @Bean
//    public TaskScheduler taskScheduler(){
//        return new ThreadPoolTaskScheduler();
//    }
//
//}
