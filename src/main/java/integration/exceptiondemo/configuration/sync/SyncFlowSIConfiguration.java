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
public class SyncFlowSIConfiguration {

    @Bean
    @Qualifier("DirectMessageChannel")
    public MessageChannel DirectMessageChannel() {
        return new DirectChannel();
    }

    @MessagingGateway
    public interface MyGateway {

        @Gateway(requestChannel = "DirectMessageChannel")
        String sendDemoMessage(Message message);

    }

    @Bean
    public IntegrationFlow HandleFlow() {
        return IntegrationFlows.from(DirectMessageChannel())
                .handle(p -> System.out.println("checking: " + p.getPayload()))
//                .handle(doHandle())
                .get();
    }


    @Bean
    public MessageHandler doHandle() {

        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println("Handled: " + message.getPayload());
            }
        };
    }
}
