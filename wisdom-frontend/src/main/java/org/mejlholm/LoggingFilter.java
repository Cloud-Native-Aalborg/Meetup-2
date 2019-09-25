package org.mejlholm;

import lombok.extern.slf4j.Slf4j;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.StreamHttpLogWriter;
import org.zalando.logbook.jaxrs.LogbookServerFilter;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

@Provider
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter, WriterInterceptor {

    private Logbook logbook = Logbook.builder()
            .sink(new DefaultSink(
                    new JsonHttpLogFormatter(),
                    new StreamHttpLogWriter(System.out))
            ).build();

    private LogbookServerFilter logbookServerFilter = new LogbookServerFilter(logbook);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logbookServerFilter.filter(requestContext);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        logbookServerFilter.filter(requestContext, responseContext);
    }

    @Override
    public void aroundWriteTo(final WriterInterceptorContext context) throws IOException {
        logbookServerFilter.aroundWriteTo(context);
    }
}

