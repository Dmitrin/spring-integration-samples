package integration.exceptiondemo.async.globalErrorChannel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import integration.exceptiondemo.configuration.SIConfiguration;
import integration.exceptiondemo.configuration.async.globalErrorChannel.AsyncServiceActivatorGlobalErrorChannelSIConfiguration;

import java.util.concurrent.Future;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SIConfiguration.class, AsyncServiceActivatorGlobalErrorChannelSIConfiguration.class})
public class AsyncServiceActivatorGlobalErrorChannelSITests {

    @Autowired
    private AsyncServiceActivatorGlobalErrorChannelSIConfiguration.AsyncActivatorGateway asyncActivatorGateway;

    @Autowired
    @Qualifier("asyncMessageChannel")
    private PollableChannel pollableChannel;

    @Test
    public void asyncActivatorTest() throws InterruptedException {

        Future<String> result2 = asyncActivatorGateway.sendDemoFutureMessage(new GenericMessage<>("body"));
        Future<String> result = asyncActivatorGateway.sendDemoFutureMessage(new GenericMessage<>("WrongBody"));

        System.out.println("asyncActivatorTest result: " + result);
        System.out.println("asyncActivatorTest result2: " + result2);

        Thread.sleep(10000);
    }
}
