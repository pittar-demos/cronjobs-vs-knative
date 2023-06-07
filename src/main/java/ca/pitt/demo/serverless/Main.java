package ca.pitt.demo.serverless;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import ca.pitt.demo.serverless.client.WebhookService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main implements QuarkusApplication {

    @ConfigProperty(name = "init.message")
    String initMessage;

    @ConfigProperty(name = "endpoint.enabled")
    Boolean endpointEnabled;

    @Inject
    @RestClient
    WebhookService webhookService;

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Is REST endpoint enabled?");

        if (endpointEnabled.booleanValue()) {
            System.out.println("YES! App will run until manual or auto scale down.");
            Quarkus.waitForExit();
        } else {
            System.out.println("NO! App will terminate after posting to external URL.");
            webhookService.postToWebhook(initMessage);
        }
        return 0;
    }
    
}
