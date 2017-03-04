package org.tiogasolutions.lib.jaxrs.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.jaxrs.domain.TiogaExceptionInfo;
import org.tiogasolutions.pub.PubItem;
import org.tiogasolutions.pub.PubLinks;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.HashMap;
import java.util.Map;

public abstract class TiogaJaxRsExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger log = LoggerFactory.getLogger(TiogaJaxRsExceptionMapper.class);

    @Context
    protected UriInfo uriInfo;

    private final Map<Class<?>, Integer> exceptionMap = new HashMap<>();

    public TiogaJaxRsExceptionMapper() {
        log.info("Created.");
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
            int status = exceptionMap.get(ex.getClass());
            return throwableToResponse(status, ex);
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

        return throwableToResponse(500, ex);
    }

    protected Response apiExceptionToResponse(ApiException ex) {
        int status = ex.getHttpStatusCode().getCode();
        logException(ex, status);

        TiogaExceptionInfo exceptionInfo = new TiogaExceptionInfo(ex);
        return exceptionInfoToResponse(exceptionInfo);
    }

    protected Response webApplicationExceptionToResponse(WebApplicationException ex) {
        int status = ex.getResponse().getStatus();
        logException(ex, status);

        TiogaExceptionInfo exceptionInfo = new TiogaExceptionInfo(ex);
        return exceptionInfoToResponse(exceptionInfo);
    }

    protected Response throwableToResponse(int status, Throwable ex) {
        logException(ex, status);

        TiogaExceptionInfo exceptionInfo = new TiogaExceptionInfo(
                PubItem.toStatus(status),
                PubLinks.empty());
        return exceptionInfoToResponse(exceptionInfo);
    }

    protected Response exceptionInfoToResponse(TiogaExceptionInfo exceptionInfo) {
        int status = exceptionInfo.get_response().getCode();
        return Response.status(status)
                .entity(exceptionInfo)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    protected void logException(Throwable throwable, int status) {
        String msg = "Status " + status;
        if (uriInfo != null) {
            msg += " ";
            msg += uriInfo.getRequestUri();
        }

        if (status >= 400 && status < 500) {
            log4xxException(msg, throwable);
        } else {
            log5xxException(msg, throwable);
        }
    }

    @SuppressWarnings("UnusedParameters")
    protected void log4xxException(String msg, Throwable throwable) {
        log.info(msg);
    }

    protected void log5xxException(String msg, Throwable throwable) {
        log.error(msg, throwable);
    }
}
