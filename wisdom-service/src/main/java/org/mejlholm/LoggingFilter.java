package org.mejlholm;


import io.opentracing.Tracer;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.UUID;


@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class);

    @Context
    UriInfo info;

    @Context
    HttpServletRequest request;

    @Inject
    Tracer tracer;

    @Override
    public void filter(ContainerRequestContext context) {

        final String method = context.getMethod();
        final String path = info.getPath();
        final String address = request.getRemoteAddr();

        String uuid = tracer.activeSpan().getBaggageItem("uuid");
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            tracer.activeSpan().setBaggageItem("uuid", uuid);
        }

        LOG.infof("Request %s %s %s from IP %s", uuid, method, path, address);

        tracer.activeSpan().setTag("uuid", uuid).setTag("method", method).setTag("path", path).setTag("address", address);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        LOG.infof("Response %s code %s ", tracer.activeSpan().getBaggageItem("uuid"), responseContext.getStatus());
    }
}