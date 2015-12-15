/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc.championship.platform.decoder.lap.publisher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import org.openide.util.lookup.ServiceProvider;
import rc.championship.api.model.Lap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jglue.fluentjson.JsonBuilderFactory;
import org.jglue.fluentjson.JsonObjectBuilder;
import org.openide.util.Exceptions;

@ServiceProvider(service = LapPublisher.class)
public class RccLapPublisher extends LapPublisher implements Runnable {

    private ExecutorService executor;
    
    @Override
    protected String getName() {
        return "RC Championship publisher";
    }

    @Override
    protected void start() {
        super.start();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(this);
    }
    
    @Override
    public void run() {
        try {
            while (isEnabled() && isStarted()) {
                List<Lap> lapsToPublish = new ArrayList<>();
                super.publishQueue.drainTo(lapsToPublish);
                send(lapsToPublish);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void send(List<Lap> laps) {

        try {
            String json = convertToJsonArray(laps);

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://localhost:8080/api/laps");
            StringEntity input = new StringEntity(json);
            post.setEntity(input);
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    static String convertToJsonArray(List<Lap> laps) {
        org.jglue.fluentjson.JsonArrayBuilder<?, JsonArray> arrBuilder = JsonBuilderFactory.buildArray();
        for (Lap lap : laps) {
            JsonObjectBuilder<?, JsonObject> lapBuilder = JsonBuilderFactory.buildObject();
            if (lap.getId().isPresent()) {
                lapBuilder.add("lapid", lap.getId().get());
            }
            if (lap.getNumber().isPresent()) {
                lapBuilder.add("number", lap.getNumber().get());
            }
            if (lap.getTime().isPresent()) {
                lapBuilder.add("time", lap.getTime().get());
            }
            if (lap.getStrength().isPresent()) {
                lapBuilder.add("strength", lap.getStrength().get());
            }
            if (lap.getHit().isPresent()) {
                lapBuilder.add("hit", lap.getHit().get());
            }
            if (lap.getDecoderId().isPresent()) {
                lapBuilder.add("decoderId", lap.getDecoderId().get());
            }
            if (lap.getVoltage().isPresent()) {
                lapBuilder.add("voltage", lap.getVoltage().get());
            }
            if (lap.getTemprature().isPresent()) {
                lapBuilder.add("temprature", lap.getTemprature().get());
            }
            if (lap.getTransponder().isPresent()) {
                lapBuilder.add("transponder", lap.getTransponder().get());
            }
            arrBuilder.add(lapBuilder);
        }

        arrBuilder.end();
        JsonArray jsonArr = arrBuilder.getJson();
        String json = jsonArr.toString();
        return json;
    }

}
