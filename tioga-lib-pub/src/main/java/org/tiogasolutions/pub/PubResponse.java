package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

public class PubResponse {

    private final int code;
    private final String message;

    @JsonCreator
    public PubResponse(@JsonProperty("code") int code,
                       @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public PubResponse(HttpStatusCode statusCode) {
        if (statusCode == null) {
            statusCode = HttpStatusCode.UNDEFINED;
        }

        this.code = statusCode.getCode();
        this.message = statusCode.getReason();
    }

    public PubResponse(HttpStatusCode statusCode, String message) {
        if (statusCode == null) {
            statusCode = HttpStatusCode.UNDEFINED;
        }

        this.code = statusCode.getCode();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
