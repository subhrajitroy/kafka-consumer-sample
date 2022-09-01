import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration  {

    @Bean
    public TestConsumer testConsumer(){
        return new TestConsumer();
    }

}
