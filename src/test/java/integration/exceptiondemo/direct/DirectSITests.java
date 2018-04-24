package integration.exceptiondemo.direct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import integration.exceptiondemo.configuration.direct.DirectSIConfiguration;
import integration.exceptiondemo.configuration.SIConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {DirectSIConfiguration.class, SIConfiguration.class})
public class DirectSITests {

    @Autowired
    @Qualifier("outputFlow.input")
    private MessageChannel in;

    @Test
    public void contextLoads() {

        new MessagingTemplate(in).convertAndSend("foo");

    }

}
