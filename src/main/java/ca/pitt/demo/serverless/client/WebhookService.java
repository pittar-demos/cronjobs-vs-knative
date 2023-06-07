package ca.pitt.demo.serverless.client;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
public interface WebhookService {

    @POST
    void postToWebhook(String message);
    
}
