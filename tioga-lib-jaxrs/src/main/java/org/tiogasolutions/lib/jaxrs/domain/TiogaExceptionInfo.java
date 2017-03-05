package org.tiogasolutions.lib.jaxrs.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ApiException;

import javax.ws.rs.WebApplicationException;

public class TiogaExceptionInfo {

    private final int statusCode;
    private final String message;

    @JsonCreator
    public TiogaExceptionInfo(@JsonProperty("statusCode") int statusCode,
                              @JsonProperty("message") String message) {

        this.statusCode = statusCode;
        this.message = message;
    }

    public TiogaExceptionInfo(ApiException ex) {
        this.statusCode = ex.getStatusCode();
        this.message = ex.getMessage();
    }

    public TiogaExceptionInfo(WebApplicationException ex) {
        this.statusCode = ex.getResponse().getStatus();
        this.message = ex.getMessage();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
