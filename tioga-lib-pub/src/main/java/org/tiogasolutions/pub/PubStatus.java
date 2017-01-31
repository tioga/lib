package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

public class PubStatus {

    private final int code;
    private final String message;

    public PubStatus(@JsonProperty("code") int code,
                     @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public PubStatus(HttpStatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getReason();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
