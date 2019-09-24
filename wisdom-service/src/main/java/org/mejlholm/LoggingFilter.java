package org.mejlholm;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;


@Provider
@Slf4j
public class LoggingFilter implements ContainerRequestFilter {

    @Context
    UriInfo info;

    @Context
    HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext context) {

        final String method = context.getMethod();
        final String path = info.getPath();
        final String address = request.getRemoteAddr();

        log.info("Request " + method + " " + path + " from IP " + address);

    }
}
