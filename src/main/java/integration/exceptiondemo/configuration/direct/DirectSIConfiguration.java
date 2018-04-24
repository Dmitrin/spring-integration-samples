package integration.exceptiondemo.configuration.direct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * Simple DirectChannel
 *
 * Simple Logger and Handler
 */

@Configuration
@Slf4j
public class DirectSIConfiguration {

    @Bean
    @Qualifier(value = "directMessageChannel")
    public MessageChannel directMessageChannel() {
        return new DirectChannel();
    }

    @Bean
    @Qualifier(value = "errorDirectChannel")
    public MessageChannel errorDirectChannel() {
        return new DirectChannel();
    }


    @Bean
    public IntegrationFlow flow() {
        return f -> f
                .bridge()
                .handle(logger());
    }


    @Bean
    public MessageHandler logger() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println("logging is working");
                System.out.println("payload is: " + message.getPayload().toString());
            }
        };
    }


    @MessageEndpoint
    public static class MyActivators {

        @ServiceActivator(inputChannel = "input")
        public String doSomethingWithException() {
            System.out.println("doSomethingWithException starting");
            return "ServiceActivator - doSomethingWithException";
        }

    }
}
