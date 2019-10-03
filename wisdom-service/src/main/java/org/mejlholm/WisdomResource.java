package org.mejlholm;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

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
@Counted(name = "numberOfAccesses")
@Timed(name = "accessTimer", unit = MetricUnits.MILLISECONDS)
@ApplicationScoped
public class WisdomResource {

    @Inject
    TwitterScheduler twitterScheduler;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("random")
    public Response random() {

        Tweet tweet = twitterScheduler.getRandomTweet();

        JsonObject payload = Json.createObjectBuilder()
                .add("author", tweet.getAuthor())
                .add("quote", tweet.getQuote())
                .build();

        return Response.ok().entity(payload.toString()).build();
    }
}