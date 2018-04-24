package integration.exceptiondemo.router;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import integration.exceptiondemo.configuration.SIConfiguration;
import integration.exceptiondemo.configuration.router.ExceptionTypeRouter;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SIConfiguration.class, ExceptionTypeRouter.class})
public class ExceptionTypeRouterTest {

    @Autowired
    @Qualifier("inDefaultChannel")
    private DirectChannel in;

    @Test
    public void fistRouteTest() {

        in.send(new GenericMessage<>("ready!"));

        in.send(new GenericMessage<>(new NullPointerException()));
    }
}


