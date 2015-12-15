/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc.championship.platform.decoder.lap.publisher;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import static org.junit.Assert.*;
import rc.championship.api.model.Lap;

/**
 *
 * @author Stefan
 */
public class RccLapPublisherTest {
    
    public RccLapPublisherTest() {
    }

    @Test
    public void testConvertToJsonArray() {
        Optional<Long> id = Optional.of(10l);
        Optional<Long> number = Optional.of(5l);
        Optional<Date> time = Optional.empty();
        Optional<Long> strength = Optional.of(100l);
        Optional<Long> hit = Optional.of(50l);
        Optional<Long> decoderId = Optional.of(1234567l);
        Optional<Double> voltage = Optional.of(14.4);
        Optional<Double> temprature = Optional.of(55.5);
        Optional<Long> transponder = Optional.of(7654321l);
        
        Lap a = new Lap(id, number, time, strength, hit, decoderId, voltage, temprature, transponder);
        
        id = Optional.of(11l);
        number = Optional.of(6l);
        Lap b = new Lap(id, number, time, strength, hit, decoderId, voltage, temprature, transponder);
        
        String jsonArray = RccLapPublisher.convertToJsonArray(Arrays.asList(a,b));
        
        assertThat(jsonArray, Matchers.equalToIgnoringWhiteSpace(
                "[{\"lapid\":10,\"number\":5,\"strength\":100,\"hit\":50,\"decoderId\":1234567,\"voltage\":14.4,\"temprature\":55.5,\"transponder\":7654321},"
               + "{\"lapid\":11,\"number\":6,\"strength\":100,\"hit\":50,\"decoderId\":1234567,\"voltage\":14.4,\"temprature\":55.5,\"transponder\":7654321}]"));
    }
    
}
