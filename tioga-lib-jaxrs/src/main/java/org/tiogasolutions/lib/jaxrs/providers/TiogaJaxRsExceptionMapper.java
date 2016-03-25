package org.tiogasolutions.lib.jaxrs.providers;

import java.util.*;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.dev.common.exceptions.*;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.jaxrs.domain.TiogaExceptionInfo;

public abstract class TiogaJaxRsExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger log = LoggerFactory.getLogger(TiogaJaxRsExceptionMapper.class);

  @Context
  protected UriInfo uriInfo;

  private final Map<Class<?>,Integer> exceptionMap = new HashMap<>();

  public TiogaJaxRsExceptionMapper() {
    log.info("Created.");
  }

  public void registerException(HttpStatusCode httpStatus, Class<?>...types) {
    registerException(httpStatus.getCode(), types);
  }

  public void registerException(int httpStatus, Class<?>...types) {
    for (Class<?> type : types) {
      exceptionMap.put(type, httpStatus);
    }
  }

  @Override
  public Response toResponse(Throwable ex) {

    if (exceptionMap.containsKey(ex.getClass())) {
      int status = exceptionMap.get(ex.getClass());
      return toResponse(status, ex);
    }

    if (ex instanceof ApiException) {
      ApiException apiEx = (ApiException) ex;
      int status = apiEx.getHttpStatusCode().getCode();
      return toResponse(status, ex);
    }

    if (ex instanceof WebApplicationException) {
      WebApplicationException wae = (WebApplicationException)ex;
      if (wae.getResponse() != null) {
        int status = wae.getResponse().getStatus();
        return toResponse(status, ex);
      }
    }

    return toResponse(500, ex);
  }

  protected Response toResponse(int status, Throwable ex) {
    logException(ex, status);
    return createResponse(status, ex);
  }

  protected Response createResponse(int status, Throwable ex) {
    TiogaExceptionInfo exceptionInfo = new TiogaExceptionInfo(status, ex);
    return Response.status(status).entity(exceptionInfo).type(MediaType.APPLICATION_JSON_TYPE).build();
  }

  protected void logException(Throwable throwable, int status) {
    String msg = "Status " + status;
    if (uriInfo != null) {
      msg += " ";
      msg += uriInfo.getRequestUri();
    }

    if (status >= 400 &&  status < 500) {
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
