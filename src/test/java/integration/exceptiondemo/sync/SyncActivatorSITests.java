package integration.exceptiondemo.sync;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import integration.exceptiondemo.configuration.SIConfiguration;
import integration.exceptiondemo.configuration.sync.SyncActivatorSIConfiguration;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SIConfiguration.class, SyncActivatorSIConfiguration.class})
public class SyncActivatorSITests {

    @Autowired
    private SyncActivatorSIConfiguration.SyncAsyncGateway in;

    @Test
    public void testSync() {
        String result = in.sendDemoMessage(new GenericMessage<>("body"));
        System.out.println("testSync: result is: " + result);

        Assert.assertNotNull(result);
        Assert.assertEquals("body", result);
    }

    @Test
    public void testSyncError() {
        try {
            in.sendDemoMessage(new GenericMessage<>("shouldFail"));

            Assert.fail("Test should throw a Message exception!");
        } catch (Exception e) {
            System.out.println("testSyncError: Error!");
            Assert.assertEquals(e.getMessage(), "nested exception is java.io.IOException: synchronizedServiceActivator: wrong body");
            Assert.assertEquals(e.getCause().getClass(), IOException.class);
        }
    }
}
