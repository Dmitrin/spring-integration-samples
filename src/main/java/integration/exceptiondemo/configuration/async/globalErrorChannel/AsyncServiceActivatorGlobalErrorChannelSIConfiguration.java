package integration.exceptiondemo.configuration.async.globalErrorChannel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Use global ErrorChannel
 */

@Configuration
@Slf4j
public class AsyncServiceActivatorGlobalErrorChannelSIConfiguration {

    @Bean
    @Qualifier("asyncMessageChannel")
    public PollableChannel asyncMessageChannel() {
        return new QueueChannel(5);
    }

    @Bean
    @Qualifier("asyncPoller")
    public static PollerMetadata asyncPoller() {
        return Pollers.fixedDelay(2000).get();
    }

    @MessagingGateway(errorChannel = "errorChannel")
    public interface AsyncActivatorGateway {

        @Gateway(requestChannel = "asyncMessageChannel")
        Future<String> sendDemoFutureMessage(Message message);

    }

    @MessageEndpoint
    public static class MethodActivators {

        @ServiceActivator(inputChannel = "asyncMessageChannel", poller = @Poller(fixedDelay = "1000"))
        public String asyncActivator(Message message) throws IOException {
            log.warn("asynchronizedServiceActivator: Incoming message: ", message);

            if (message.getPayload().equals("body")) {
                log.warn("asynchronizedServiceActivator: Message.payload is: {}", message.getPayload());
            } else {
                log.error("asynchronizedServiceActivator: wrong body: {}", message.getPayload());
                throw new IOException("synchronizedServiceActivator: wrong body");
            }

            return "body";
        }

        @ServiceActivator(inputChannel = "errorChannel")
        public String handleFailMessage(Message message) {
            log.warn("Handling failed message!...{}", message);
            System.out.println("Failed message: " + message);
            return "ok";
        }
    }
}
