package ca.pitt.demo.serverless;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.oracle.svm.core.annotate.Inject;

import ca.pitt.demo.serverless.client.WebhookService;

@Path("/")
public class MainResource {

    @ConfigProperty(name = "post.message")
    String postMessage;

    @Inject
    @RestClient
    WebhookService webhookService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String invokeFromGet() {
        System.out.println("GET method activated.");
        webhookService.postToWebhook(postMessage);
        return "Message sent: " + postMessage;
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public void invokeFromPost() {
        System.out.println("POST method activated.");
        webhookService.postToWebhook(postMessage);
    }
}