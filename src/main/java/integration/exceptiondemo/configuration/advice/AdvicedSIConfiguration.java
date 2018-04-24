package integration.exceptiondemo.configuration.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice;
import org.springframework.integration.router.ErrorMessageExceptionTypeRouter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

/**
 * Exception routing based on Advice
 */

@Configuration
@Slf4j
public class AdvicedSIConfiguration {

    @Bean
    @Qualifier(value = "inputQueueChannel")
    public PollableChannel inputQueueChannel() {
        return new QueueChannel();
    }

    @Bean
    @Qualifier(value = "outputQueueChannel")
    public PollableChannel outputQueueChannel() {
        return new QueueChannel();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    @ConditionalOnMissingBean(value = PollerMetadata.class)
    public PollerMetadata poller() {
        return Pollers.fixedRate(5000).get();
    }

//    @Bean
//    @Qualifier(value = "errorQueueChannel")
//    public PollableChannel errorQueueChannel() {
//        QueueChannel queueChannel = new QueueChannel();
//        queueChannel.addInterceptor(new ChannelInterceptorAdapter() {
//
//            @Override
//            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
//                super.postSend(message, channel, sent);
//
//                if (message instanceof ErrorMessage) {
//                    throw (RuntimeException) ((ErrorMessage) message).getPayload();
//                }
//            }
//        });
//        return queueChannel;
//    }

    @Bean
    public IntegrationFlow splitAggregateFlow() {
        return IntegrationFlows
                .from("inputQueueChannel")
                .split(splitter -> splitter.delimiters(","))
                .<String, String>transform(String::toUpperCase)
                .aggregate()
                .channel("outputQueueChannel")
                .get();
    }

    @Bean
    public IntegrationFlow success() {
        return f -> f.handle(System.out::println);
    }

    @Bean
    public IntegrationFlow failure() {
        return f -> f.handle(System.out::println);
    }

    @Bean
    public IntegrationFlow advised() {
        return f -> f.handle(someHandler(), c -> c.advice(expressionAdvice()));
    }

    @Bean
    public GenericHandler someHandler() {
        return new GenericHandler() {
            @Override
            public Object handle(Object payload, Map headers) {
                if (payload.equals("good")) {
                    return null;
                }
                else {
                    log.error("some failure");
                    throw new RuntimeException("some failure");
                }
            }
        };
    }


    @Bean
    public Advice expressionAdvice() {
        ExpressionEvaluatingRequestHandlerAdvice advice = new ExpressionEvaluatingRequestHandlerAdvice();
        advice.setSuccessChannelName("success.input");
        advice.setOnSuccessExpressionString("payload + ' was successful'");
        advice.setFailureChannelName("failure.input");
        advice.setOnFailureExpressionString("payload + ' was bad, with reason: ' + #exception.cause.message");
        advice.setTrapException(true);

        return advice;
    }
}
