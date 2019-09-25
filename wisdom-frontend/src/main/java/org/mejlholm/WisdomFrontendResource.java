package org.mejlholm;

import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("wisdom")
@Metered
@ApplicationScoped
public class WisdomFrontendResource {

    @Inject
    @RestClient
    WisdomClient wisdomClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("random")
    public Response random() {
        return wisdomClient.random();
    }
}