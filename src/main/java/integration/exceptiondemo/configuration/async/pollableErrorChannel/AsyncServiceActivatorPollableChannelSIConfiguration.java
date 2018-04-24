package integration.exceptiondemo.configuration.async.pollableErrorChannel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;

import java.io.IOException;
import java.util.concurrent.Future;

/**
* This don't work. Still need to fix
*/

@Configuration
@Slf4j
public class AsyncServiceActivatorPollableChannelSIConfiguration {

    @Bean
    @Qualifier("asyncMessageChannel")
    public PollableChannel asyncMessageChannel() {
        return new QueueChannel(5);
    }

    @Bean
    @Qualifier("asyncErrorChannel")
    public PollableChannel asyncErrorChannel() {
        return MessageChannels.queue(5).get();
    }

    @Bean
    @Qualifier("asyncPoller")
    public static PollerMetadata asyncPoller() {
        return Pollers.fixedDelay(2000).get();
    }

    @MessagingGateway(errorChannel = "asyncErrorChannel")
    public interface AsyncActivatorGateway {

        @Gateway(requestChannel = "asyncMessageChannel")
        Future<String> sendDemoFutureMessage(Message message);

    }

    @MessageEndpoint
    public static class MethodActivators {

//        @InboundChannelAdapter(channel = "asyncMessageChannel", poller = @Poller(value = "asyncPoller"))
//        @InboundChannelAdapter(channel = "asyncMessageChannel", poller = @Poller(fixedDelay = "1000"))
//        public String gen() throws InterruptedException {
//            log.error("InboundChannelAdapter: I see the message!");
//            return "foo";
//        }

        @ServiceActivator(inputChannel = "asyncMessageChannel", poller = @Poller(fixedDelay = "1000"))
        public String asyncActivator(Message message) throws IOException {
            log.warn("asynchronizedServiceActivator: Incoming message: ", message.getPayload());

            if (message.getPayload().equals("body")) {
                log.warn("asynchronizedServiceActivator: Message.payload is: {}", message.getPayload());
            } else {
                log.error("asynchronizedServiceActivator: wrong body: {}", message.getPayload());
                throw new IOException("synchronizedServiceActivator: wrong body");
            }

            return "body";
        }

        @ServiceActivator(inputChannel = "asyncErrorChannel", poller = @Poller(fixedDelay = "1000"))
        public String asyncHandleFailMessage(Message message) {
            log.warn("asyncHandleFailMessage!... {}", message);
            System.out.println("asyncHandleFailMessage Failed message: " + message);
            return "ok";
        }
    }
}
