package no.westerdals.quiz;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestITBase {
    private static ResteasyClient getClient() {
        // Setting digest credentials
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "admin");
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        HttpClient httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpclient, true);

        return new ResteasyClientBuilder().httpEngine(engine).build();
    }

    public static boolean isJBossUpAndRunning() {
        try {
            WebTarget target = getClient()
                    .target("http://localhost:9990/management")
                    .queryParam("operation", "attribute")
                    .queryParam("name", "server-state");

            Response response = target.request(MediaType.APPLICATION_JSON).get();

            return response.getStatus() == Response.Status.OK.getStatusCode() && response.readEntity(String.class).contains("running");

        } catch (Exception e){
            return false;
        }
    }

    public static void waitForJBoss(int seconds){
        for (int i = 0; i < seconds && !isJBossUpAndRunning(); i++) {
            try {
                Thread.sleep(1_000); //check every second
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
