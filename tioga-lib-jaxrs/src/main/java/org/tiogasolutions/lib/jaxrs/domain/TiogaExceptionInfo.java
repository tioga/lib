package org.tiogasolutions.lib.jaxrs.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

public class TiogaExceptionInfo {

    private final int code;
    private final String message;

    @JsonCreator
    public TiogaExceptionInfo(@JsonProperty("code") int code,
                              @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public TiogaExceptionInfo(int code, Throwable ex) {
        this.code = code;
        this.message = ExceptionUtils.getMessage(ex);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
