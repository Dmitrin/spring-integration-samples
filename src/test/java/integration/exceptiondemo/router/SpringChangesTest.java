package integration.exceptiondemo.router;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.router.ErrorMessageExceptionTypeRouter;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

import static org.junit.Assert.assertNotNull;

/**
 * [Spring JIRA] (INT-4455) NPE fix Test
 */
public class SpringChangesTest {

    @Test
    public void testLateClassBinding() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);

        ctx.getBean(ErrorMessageExceptionTypeRouter.class).handleMessage(new GenericMessage<>(new NullPointerException()));
        assertNotNull(ctx.getBean("channel", PollableChannel.class).receive(0));
        ctx.close();
    }

    public static class Config {

        @Bean
        public ErrorMessageExceptionTypeRouter errorMessageExceptionTypeRouter() {
            ErrorMessageExceptionTypeRouter router = new ErrorMessageExceptionTypeRouter();
            router.setChannelMapping(NullPointerException.class.getName(), "channel");
            return router;
        }

        @Bean
        public PollableChannel channel() {
            return new QueueChannel();
        }

    }
}
