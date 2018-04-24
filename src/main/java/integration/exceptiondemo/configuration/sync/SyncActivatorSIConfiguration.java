package integration.exceptiondemo.configuration.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.io.IOException;

@Configuration
@Slf4j
public class SyncActivatorSIConfiguration {

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

    @MessageEndpoint
    public static class MethodActivators {

        @ServiceActivator(inputChannel = "synchronizedMessageChannel")
        public String synchronizedServiceActivator(Message<?> message) throws IOException {
            log.debug("synchronizedServiceActivator: Incoming message: ", message);

            if (message.getPayload().equals("body")) {
                log.debug("synchronizedServiceActivator: Message.payload is: {}", message.getPayload());
            } else {
                log.error("synchronizedServiceActivator: wrong body: {}", message.getPayload());
                throw new IOException("synchronizedServiceActivator: wrong body");
            }

            return "body";
        }
    }
}
