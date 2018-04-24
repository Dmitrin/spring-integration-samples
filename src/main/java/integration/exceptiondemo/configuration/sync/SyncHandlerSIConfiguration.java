package integration.exceptiondemo.configuration.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Configuration
@Slf4j
public class SyncHandlerSIConfiguration {

    @Bean
    @Qualifier("DirectMessageChannel")
    public MessageChannel synchronizedMessageChannel() {
        return new DirectChannel();
    }

    @MessagingGateway
    public interface SyncAsyncGateway {

        @Gateway(requestChannel = "synchronizedMessageChannel")
        String sendDemoMessage(Message message);

    }

    @Bean
    public IntegrationFlow synchFlow() {
        return IntegrationFlows.from(synchronizedMessageChannel())
                .handle(logger())
                .get();
    }

    @Bean
    public MessageHandler logger() {

        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println("logger: logging is working");
                System.out.println("logger: payload is: " + message.getPayload().toString());

                throw new MessagingException("something happend!");
//                throw new RuntimeException("something happend!");
            }
        };
    }
}
