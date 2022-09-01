import com.roy.examples.Application;
import com.roy.examples.EventPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootConfiguration
@SpringBootTest(classes = {Application.class,TestConfiguration.class})
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        zookeeperPort = 2182)
public class KafkaConsumerIntegrationTest {


    @Autowired
    private TestConsumer consumer;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @BeforeEach
    public void setUp(){
        consumer.reset();
    }

    @Test
    public void shouldMoveEventToDLTWhenFataExceptionIsThrown() throws InterruptedException {
        final EventPublisher eventPublisher = new EventPublisher(kafkaTemplate);
        /**
         * This delay ensures that consumer is awake before event is produced
         * otherwise we will need to set listener to start processing events from start
         */
        publishMessageAfterWait(eventPublisher, "never");
        Thread.sleep(2000L);
        Assertions.assertFalse(consumer.getNormalEvents().isEmpty());
        Assertions.assertFalse(consumer.getDltEvents().isEmpty());
    }



    @Test
    public void shouldNotMoveEventToDLTWhenFataExceptionIsNotThrown() throws InterruptedException {
        final EventPublisher eventPublisher = new EventPublisher(kafkaTemplate);
        /**
         * This delay ensures that consumer is awake before event is produced
         * otherwise we will need to set listener to start processing events from start
         */
        publishMessageAfterWait(eventPublisher, "random");
        Thread.sleep(2000L);
        Assertions.assertFalse(consumer.getNormalEvents().isEmpty());
        Assertions.assertTrue(consumer.getDltEvents().isEmpty());
    }

    private void publishMessageAfterWait(EventPublisher eventPublisher, String event) throws InterruptedException {
        Thread.sleep(200L);
        eventPublisher.publishMesssage(event, "kyc");
    }

}
