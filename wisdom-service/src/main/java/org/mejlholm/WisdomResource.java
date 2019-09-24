package org.mejlholm;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/wisdom")
@Metered
@ApplicationScoped
public class WisdomResource {

    @Inject
    TwitterScheduler twitterScheduler;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("random")
    @CircuitBreaker(requestVolumeThreshold = 10)
    public Response random() {

        JsonObject payload = Json.createObjectBuilder()
                .add("message", twitterScheduler.getRandomTweet())
                .build();

        return Response.ok().entity(payload.toString()).build();
    }
}