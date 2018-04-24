package integration.exceptiondemo.sync;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import integration.exceptiondemo.configuration.SIConfiguration;
import integration.exceptiondemo.configuration.sync.SyncFlowSIConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SIConfiguration.class, SyncFlowSIConfiguration.class})
public class SyncFlowSITest {

    @Autowired
    private SyncFlowSIConfiguration.MyGateway myGateway;

    @Test
    public void checkFlowSITest() {

        myGateway.sendDemoMessage(new GenericMessage<>("doIt!"));

    }

}
