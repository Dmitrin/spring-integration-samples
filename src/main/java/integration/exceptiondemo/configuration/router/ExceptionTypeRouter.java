package integration.exceptiondemo.configuration.router;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.router.ErrorMessageExceptionTypeRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Queue ErrorChannel
 *
 * ErrorMessageExceptionTypeRouter with PollableChannel
 */

@Configuration
@Slf4j
public class ExceptionTypeRouter {

    @Bean
    @Qualifier("inDefaultChannel")
    public DirectChannel inDefaultChannel() {
        return MessageChannels
                .direct()
                .get();
    }

    @Bean
    @Qualifier("outDefaultChannel")
    public DirectChannel outDefaultChannel() {
        return MessageChannels
                .direct()
                .get();
    }

    @Bean
    @Qualifier("asyncErrorChannel")
    public PollableChannel asyncErrorChannel() {
        return MessageChannels
                .queue(5)
                .get();
    }

//    @Bean
//    @Qualifier("asyncErrorChannel")
//    public DirectChannel asyncErrorChannel() {
//        return MessageChannels
//                .direct()
//                .get();
//    }

//    @Bean
//    public ErrorMessageExceptionTypeRouter errorMessageExceptionTypeRouter() {
//        ErrorMessageExceptionTypeRouter router = new ErrorMessageExceptionTypeRouter();
//        router.setChannelMapping(IOException.class.getName(), "asyncErrorChannel");
//        router.setChannelMapping(NullPointerException.class.getName(), "asyncErrorChannel");
//        router.setDefaultOutputChannel(outDefaultChannel());
//
//        return router;
//    }

    @Bean
    public ErrorMessageExceptionTypeRouter errorMessageExceptionTypeRouter() {
        ErrorMessageExceptionTypeRouter router = new ErrorMessageExceptionTypeRouter();

        Map<String, String> mappings = new HashMap<>();
        mappings.put(IOException.class.getName(), "asyncErrorChannel");
        mappings.put(NullPointerException.class.getName(), "asyncErrorChannel");

        router.setChannelMappings(mappings);
        router.setDefaultOutputChannel(outDefaultChannel());

        return router;
    }


    @Bean
    public IntegrationFlow flow() {
        return IntegrationFlows
                .from("inDefaultChannel")
                .route(errorMessageExceptionTypeRouter())
                .get();
    }

    @Bean
    public IntegrationFlow outputFlow() {
        return IntegrationFlows
                .from("outDefaultChannel")
                .log()
                .get();
    }

    @MessageEndpoint
    public static class MethodActivators {

        //@ServiceActivator(inputChannel = "asyncErrorChannel")
        @ServiceActivator(inputChannel = "asyncErrorChannel", poller = @Poller(fixedDelay = "1000"))
        public void catchExceptionFromAsyncChannel(Message message) {

            log.warn("Got message from Async channel! {}", message.getPayload());

        }

    }
}

