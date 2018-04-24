package integration.exceptiondemo.advice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import integration.exceptiondemo.configuration.SIConfiguration;
import integration.exceptiondemo.configuration.advice.AdvicedSIConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {AdvicedSIConfiguration.class, SIConfiguration.class})
public class AdvicedSITests {

    @Autowired
    @Qualifier(value = "inputQueueChannel")
    private PollableChannel input;

    @Autowired
    @Qualifier(value = "outputQueueChannel")
    private PollableChannel output;

    @Autowired
    @Qualifier(value = "advised.input")
    private MessageChannel advicedChannel;


    @Test
    public void testJavaDslFlow() {
        this.input.send(new GenericMessage<Object>("a,b,c,d"));

        Message<?> message = this.output.receive(10_000);

        assertNotNull(message);

        assertEquals("[A, B, C, D]", message.getPayload().toString());

        System.out.println(message);
    }


    @Test
    public void testAdvicedFlow() {
        advicedChannel.send(new GenericMessage<>("good"));
        advicedChannel.send(new GenericMessage<>("bad"));
    }
}
