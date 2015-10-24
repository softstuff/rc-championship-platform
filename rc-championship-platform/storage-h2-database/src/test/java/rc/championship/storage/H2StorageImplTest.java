package rc.championship.storage;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;
import rc.championship.api.model.Lap;
import rc.championship.api.services.decoder.StoredMessage;

/**
 *
 * @author Stefan
 */
public class H2StorageImplTest {
    
    private static H2StorageImpl instance;
    
    private static StoredMessage msg1;
    private static StoredMessage msg2;
    private static StoredMessage msg3;
    private static StoredMessage msg4;
    private static StoredMessage msg5;
    
    @BeforeClass
    public static void setup(){
        instance = new H2StorageImpl("jdbc:h2:mem:test");
        
        msg1 = createHistoryItem(20l,"type","command", "data");
        msg2 = createHistoryItem(21l,"type","command", "data");
        msg3 = createHistoryItem(22l,"type","command", "data");
        msg4 = createHistoryItem(23l,"type","command", "data");
        msg5 = createHistoryItem(24l,"type","command", "data");
    }
    
    @AfterClass
    public static void teardown(){
        instance.shutdown();
    }
    
    

    @Test
    public void testPropertyExpectInsertUpdateAndDeleteBehavior() {
        String key = "testKey";
        String actual = instance.getProperty(key, null);
        assertThat(actual, nullValue());
        
        String expected = "expected";
        boolean stored = instance.setProperty(key, expected);
        assertThat(stored, is(true));
        
        actual = instance.getProperty(key, null);
        assertThat(actual, is(expected));
        
        stored = instance.setProperty(key, null);
        assertThat(stored, is(true));
        
        actual = instance.getProperty(key, null);
        assertThat(actual, nullValue());
    }

    @Test
    public void storeMessage() {
        StoredMessage msgToStore = createHistoryItem(12l,"type","command", "data");
        
        List<StoredMessage> stored = instance.getMessagesPageAfter(msgToStore.getId()-1, 1);
        assertThat(stored.size(), is(1));
        
        StoredMessage readMsg = stored.get(0);
        assertStoredMessage(readMsg, msgToStore);
        
    }
    
    @Test
    public void testGetMessagesPageAfter() {
        
        List<StoredMessage> stored = instance.getMessagesPageAfter(msg2.getId(), 2);
        assertThat(stored.size(), is(2));
        assertStoredMessage(stored.get(0), msg3);
        assertStoredMessage(stored.get(1), msg4);   
    }

    @Test
    public void testGetMessagesPageBefore() {
        
        List<StoredMessage> stored = instance.getMessagesPageBefore(msg4.getId(), 2);
        assertThat(stored.size(), is(2));
        assertStoredMessage(stored.get(0), msg3);
        assertStoredMessage(stored.get(1), msg2);   
    }
    
    @Test
    public void testGetMessagesAfterExpectInclusive() {
                
        List<StoredMessage> stored = instance.getMessagesAfter(msg2.getTime(), false, 2);
        assertThat(stored.size(), is(2));
        assertStoredMessage(stored.get(0), msg2);
        assertStoredMessage(stored.get(1), msg3);   
    }
    
    @Test
    public void testGetMessagesAfterExpectExclusive() {
        
        List<StoredMessage> stored = instance.getMessagesAfter(msg2.getTime(), true, 2);
        assertThat(stored.size(), is(2));
        assertStoredMessage(stored.get(0), msg3);
        assertStoredMessage(stored.get(1), msg4);   
    }
    
    @Test
    public void testGetMessagesBeforeExpectInclusive() {
                
        List<StoredMessage> stored = instance.getMessagesBefore(msg4.getTime(), false, 2);
        assertThat(stored.size(), is(2));
        assertStoredMessage(stored.get(0), msg4);
        assertStoredMessage(stored.get(1), msg3);   
    }
    
    @Test
    public void testGetMessagesBeforeExpectExclusive() {
        
        List<StoredMessage> stored = instance.getMessagesBefore(msg4.getTime(), true, 2);
        assertThat(stored.size(), is(2));
        assertStoredMessage(stored.get(0), msg3);
        assertStoredMessage(stored.get(1), msg2);   
    }
    
    @Test
    public void testStoreLap(){
        Lap lap = createLap();
        boolean stored = instance.storeLap(lap);
        
        assertThat(lap.getId().isPresent(), is(true));
        assertThat(stored, is(true));
    }
    
    
    
    
    private void assertStoredMessage(StoredMessage actual, StoredMessage expected) {
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getTime(), is(expected.getTime()));
        assertThat(actual.getType(), is(expected.getType()));
        assertThat(actual.getCommand(), is(expected.getCommand()));
        assertThat(actual.getData(), is(expected.getData()));
    }
    
    

    private static StoredMessage createHistoryItem(long time, String type, String command, String data) {
        StoredMessage msgToStore = new StoredMessage()
                .time(time)
                .type(type)
                .command(command)
                .data(data);
        boolean success = instance.storeMessage(msgToStore);
        assertThat(success, is(true));
        return msgToStore;
    }

    private Lap createLap() {
        Lap lap = new Lap();
        lap.setTransponder(Optional.of(10l));
        lap.setTime(Optional.of(new Date()));
        lap.setDecoderId(Optional.of(20l));
        lap.setHit(Optional.of(90l));
        lap.setNumber(Optional.of(100l));
        lap.setStrength(Optional.of(50l));
        lap.setTemprature(Optional.of(55.5d));
        lap.setVoltage(Optional.of(14.4d));
        return lap;
    }
    
    
}
