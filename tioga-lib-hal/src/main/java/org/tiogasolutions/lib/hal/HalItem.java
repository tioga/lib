package org.tiogasolutions.lib.hal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

@JsonPropertyOrder({ "_response", "_links" })
public class HalItem {

    @JsonIgnore
    private final HttpStatusCode httpStatusCode;

    private final HalLinks _links;

    @JsonCreator
    protected HalItem(@JsonProperty("_links") HalLinks _links) {
        this.httpStatusCode = HttpStatusCode.UNDEFINED;
        this._links = (_links != null) ? _links : HalLinks.empty();
    }

    public HalItem(int code) {
        this(HttpStatusCode.findByCode(code), HalLinks.empty());
    }

    public HalItem(int code, HalLinks _links) {
        this(HttpStatusCode.findByCode(code), _links);
    }

    public HalItem(HttpStatusCode statusCode) {
        this(statusCode, HalLinks.empty());
    }

    public HalItem(HttpStatusCode statusCode, HalLinks _links) {
        this.httpStatusCode = statusCode;

        if (_links != null) {
            this._links = _links;
        } else {
            this._links = HalLinks.empty();
        }
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public HalLinks get_links() {
        return _links;
    }

    public HalLink getLink(String rel) {
        return _links.getLink(rel);
    }
}
