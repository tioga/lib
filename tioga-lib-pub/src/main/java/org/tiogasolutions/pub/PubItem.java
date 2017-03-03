package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

@JsonPropertyOrder({ "_response", "_links" })
public class PubItem {

    private final PubResponse _response;
    private final PubLinks _links;

    @JsonCreator
    protected PubItem(@JsonProperty("_status") PubResponse _response,
                      @JsonProperty("_links") PubLinks _links) {

        this._response = (_response != null) ? _response : new PubResponse(HttpStatusCode.UNDEFINED);
        this._links = (_links != null) ? _links : PubLinks.empty();
    }

    public PubItem(PubResponse _response) {
        this(_response, null);
    }

    public PubItem(int code) {
        this(code, null);
    }

    public PubItem(int code, PubLinks _links) {
        this(toStatus(code), _links);
    }

    public PubItem(HttpStatusCode statusCode) {
        this(statusCode, null);
    }

    public PubItem(HttpStatusCode statusCode, PubLinks _links) {
        this(toStatus(statusCode), _links);
    }

    public PubLinks get_links() {
        return _links;
    }

    public PubResponse get_response() {
        return _response;
    }

    public PubLink getLink(String rel) {
        return _links.getLink(rel);
    }

    public static PubResponse toStatus(int code) {
        HttpStatusCode statusCode = HttpStatusCode.findByCode(code);
        return new PubResponse(statusCode);
    }

    public static PubResponse toStatus(HttpStatusCode statusCode) {
        return new PubResponse(statusCode);
    }
}
