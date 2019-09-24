package org.mejlholm;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;

@Path("/wisdom")
@RegisterRestClient
public interface WisdomClient {

    @GET
    @Path("/random")
    @Produces(MediaType.APPLICATION_JSON)
    @Timeout(unit = ChronoUnit.MILLIS, value = 50)
    @Retry(maxRetries = 3)
    CompletionStage<Response> random();

}
