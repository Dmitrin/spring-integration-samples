package integration.exceptiondemo.sync;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import integration.exceptiondemo.configuration.SIConfiguration;
import integration.exceptiondemo.configuration.sync.SyncActivatorSIConfiguration;
import integration.exceptiondemo.configuration.sync.SyncHandlerSIConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SIConfiguration.class, SyncHandlerSIConfiguration.class})
public class SyncHandlerTest {

    private String ERROR_MSG = "shouldFail";
    private String ERROR_CAUSE_MSG = "something happend!";

    @Autowired
    private SyncActivatorSIConfiguration.SyncAsyncGateway in;

    @Test
    public void syncHandlerTest() {
        try {
            in.sendDemoMessage(new GenericMessage<>(ERROR_MSG));

            Assert.fail("Test should throw a Message exception!");
        } catch (Exception e) {
            System.out.println("testSyncError: Error!");
            Assert.assertEquals(e.getCause().getMessage(), ERROR_CAUSE_MSG);
            Assert.assertEquals(((MessagingException) e).getFailedMessage().getPayload(), ERROR_MSG);
            Assert.assertEquals(e.getCause().getClass(), MessagingException.class);
        }
    }
}
