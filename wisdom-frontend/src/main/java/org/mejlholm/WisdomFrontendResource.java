package org.mejlholm;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.StreamHttpLogWriter;
import org.zalando.logbook.jaxrs.LogbookClientFilter;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("wisdom")
@Metered
@ApplicationScoped
@Slf4j
public class WisdomFrontendResource {

    private Logbook logbook = Logbook.builder()
            .sink(new DefaultSink(
                    new JsonHttpLogFormatter(),
                    new StreamHttpLogWriter(System.out))
            ).build();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("random")
    //@CircuitBreaker(requestVolumeThreshold = 10)
    public Response random() throws URISyntaxException {
        WisdomClient wisdomClient = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://wisdom.mejlholm.org")) // FIXME: 9/25/19 make configurable
                .register(new LogbookClientFilter(logbook))
                .build(WisdomClient.class);

        return wisdomClient.random();
    }
}