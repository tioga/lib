package org.tiogasolutions.lib.jaxrs;

import java.util.*;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import org.tiogasolutions.dev.common.exceptions.*;

public abstract class TiogaJaxRsExceptionMapper implements ExceptionMapper<Throwable> {

  @Context
  protected UriInfo uriInfo;

  protected abstract void logInfo(String msg, Throwable ex);
  protected abstract void logError(String msg, Throwable ex);

  private final boolean renderAsJson;
  private final Map<String,Integer> exceptionMap = new HashMap<>();

  public TiogaJaxRsExceptionMapper(boolean renderAsJson) {
    this.renderAsJson = renderAsJson;
    logInfo("Created exception mapper", null);
  }

  @Override
  public Response toResponse(Throwable ex) {

    String exceptionName = ex.getClass().getName();
    if (exceptionMap.containsKey(exceptionName)) {
      int status = exceptionMap.get(exceptionName);
      return createResponse(status, ex);
    }

    if (ex instanceof ApiException) {
      ApiException apiEx = (ApiException)ex;
      int status = apiEx.getHttpStatusCode().getCode();
      return createResponse(status, ex);

    } else if (ex instanceof WebApplicationException) {
      WebApplicationException wae = (WebApplicationException)ex;
      if (wae.getResponse() != null) {
        int status = wae.getResponse().getStatus();
        return createResponse(status, ex);
      }
    }

    return createResponse(500, ex);
  }

  protected void logException(Throwable throwable, int status) {
    String msg = "Status " + status;
    if (uriInfo != null) {
      msg += " ";
      msg += uriInfo.getRequestUri();
    }

    if (status >= 400 || status < 500) {
      logInfo(msg, throwable);
    } else {
      logError(msg, throwable);
    }
  }

  protected Response createResponse(int status, Throwable ex) {

    logException(ex, status);
    String message = ExceptionUtils.getMessage(ex);

    if (renderAsJson) {
      message = message.replace("\\", "\\\\");
      message = message.replace("\"", "\\\"");
      String json = String.format(JSON_TEMPLATE, status, message);
      return Response.status(status).entity(json).type(MediaType.APPLICATION_JSON).build();
    } else {
      return Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build();
    }
  }

  private static final String JSON_TEMPLATE = "{\n" +
    "  \"status\" : \"%s\",\n" +
    "  \"message\" : \"%s\"\n" +
    "}";
}
