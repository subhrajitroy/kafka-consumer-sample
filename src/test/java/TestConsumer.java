import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableKafka
public class TestConsumer {

    private List<String> normalEvents;
    private List<String> dltEvents;

    public TestConsumer() {
        this.normalEvents = new ArrayList<>();
        this.dltEvents = new ArrayList<>();
    }

    @KafkaListener(topics = "kyc.DLT", groupId = "tests", containerFactory = "kafkaListenerContainerFactoryWithExpBackOff")
    public void processDLT(ConsumerRecord<String, String> data) {
        final String eventData = data.value();
        System.out.println("Test consumer " + eventData);
        this.dltEvents.add(eventData);
    }

    @KafkaListener(topics = "kyc", groupId = "tests", containerFactory = "kafkaListenerContainerFactoryWithExpBackOff")
    public void process(ConsumerRecord<String, String> data) {
        final String eventData = data.value();
        System.out.println("Test consumer " + eventData);
        this.normalEvents.add(eventData);
    }

    public List<String> getNormalEvents() {
        return normalEvents;
    }

    public List<String> getDltEvents() {
        return dltEvents;
    }

    public void reset() {
        normalEvents.clear();
        dltEvents.clear();
    }
}
