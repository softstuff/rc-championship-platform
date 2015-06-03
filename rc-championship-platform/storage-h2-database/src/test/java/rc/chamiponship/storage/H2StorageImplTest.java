package rc.championship.storage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Stefan
 */
public class H2StorageImplTest {
    
    private H2StorageImpl instance;
    @Before
    public void setUp() {
        instance = new H2StorageImpl("jdbc:h2:mem:test");
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

    
}
