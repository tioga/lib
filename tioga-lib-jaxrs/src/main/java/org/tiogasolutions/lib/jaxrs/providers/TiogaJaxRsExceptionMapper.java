package org.tiogasolutions.lib.jaxrs.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.jaxrs.domain.TiogaExceptionInfo;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ExceptionMapper;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public abstract class TiogaJaxRsExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger log = LoggerFactory.getLogger(TiogaJaxRsExceptionMapper.class);

    @Context
    protected UriInfo uriInfo;

    private final Map<Class<?>, Integer> exceptionMap = new HashMap<>();

    public TiogaJaxRsExceptionMapper() {
        log.info("Created.");
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public Map<Class<?>, Integer> getExceptionMap() {
        return unmodifiableMap(exceptionMap);
    }

    public void registerException(HttpStatusCode httpStatus, Class<?>... types) {
        registerException(httpStatus.getCode(), types);
    }

    public void registerException(int httpStatus, Class<?>... types) {
        for (Class<?> type : types) {
            exceptionMap.put(type, httpStatus);
        }
    }

    @Override
    public Response toResponse(Throwable ex) {

        if (exceptionMap.containsKey(ex.getClass())) {
            int statusCode = exceptionMap.get(ex.getClass());
            return throwableToResponse(statusCode, ex);
        }

        if (ex instanceof ApiException) {
            ApiException apiEx = (ApiException) ex;
            return apiExceptionToResponse(apiEx);
        }

        if (ex instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) ex;
            if (wae.getResponse() != null) {
                return webApplicationExceptionToResponse(wae);
            }
        }

        // Before giving up, check the exception's causes for
        // a better response (just in case it got wrapped)
        while (ex.getCause() != null && ex.getCause() != ex) {
            ex = ex.getCause();
            if (ex instanceof ApiException) {
                return toResponse(ex);
            } else if (ex instanceof WebApplicationException) {
                return toResponse(ex);
            } else if (exceptionMap.containsKey(ex.getClass())) {
                return toResponse(ex);
            }
        }

        return throwableToResponse(500, ex);
    }

    protected Response apiExceptionToResponse(ApiException ex) {
        int statusCode = ex.getHttpStatusCode().getCode();
        logException(ex, statusCode);

        TiogaExceptionInfo exceptionInfo = new TiogaExceptionInfo(ex);
        return exceptionInfoToResponse(statusCode, exceptionInfo);
    }

    protected Response webApplicationExceptionToResponse(WebApplicationException ex) {
        int statusCode = ex.getResponse().getStatus();
        logException(ex, statusCode);

        TiogaExceptionInfo exceptionInfo = new TiogaExceptionInfo(ex);
        return exceptionInfoToResponse(statusCode, exceptionInfo);
    }

    protected Response throwableToResponse(int statusCode, Throwable ex) {
        HttpStatusCode hsc = HttpStatusCode.findByCode(statusCode);
        logException(ex, statusCode);

        TiogaExceptionInfo exceptionInfo = new TiogaExceptionInfo(hsc.getCode(), hsc.getReason());
        return exceptionInfoToResponse(statusCode, exceptionInfo);
    }

    protected Response exceptionInfoToResponse(int statusCode, TiogaExceptionInfo exceptionInfo) {
        return Response.status(statusCode)
                .entity(exceptionInfo)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    protected void logException(Throwable throwable, int statusCode) {
        String msg = "Status " + statusCode;
        if (uriInfo != null) {
            msg += " ";
            msg += uriInfo.getRequestUri();
        }

        if (statusCode >= 400 && statusCode < 500) {
            log4xxException(msg, throwable, statusCode);
        } else {
            log5xxException(msg, throwable, statusCode);
        }
    }

    @SuppressWarnings("UnusedParameters")
    protected void log4xxException(String msg, Throwable throwable, int statusCode) {
        log.info(msg);
    }

    @SuppressWarnings("UnusedParameters")
    protected void log5xxException(String msg, Throwable throwable, int statusCode) {
        log.error(msg, throwable);
    }

    /**
     * Need to alter the URI so that it's technically not valid.
     * Slack will re-fetch this URI if we include it here.
     * @param uri The URI to be cleaned.
     * @return The URI minus the prefix http:// or https://
     */
    public static String cleanUrl(URI uri) {
        if (uri == null) {
            return null;
        } else if (uri.toASCIIString().toLowerCase().startsWith("http://")) {
            return uri.toASCIIString().substring(7);
        } else if (uri.toASCIIString().toLowerCase().startsWith("https://")) {
            return uri.toASCIIString().substring(8);
        } else {
            return uri.toASCIIString();
        }
    }
}
