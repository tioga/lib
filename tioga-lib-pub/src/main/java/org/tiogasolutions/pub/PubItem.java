package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

public class PubItem {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final PubStatus _status;

    private final PubLinks _links;

    public PubItem(PubStatus _status) {
        this._links = PubLinks.empty();
        this._status = _status;
    }

    @JsonCreator
    public PubItem(@JsonProperty("_status") PubStatus _status,
                   @JsonProperty("_links") PubLinks _links) {
        this._links = _links;
        this._status = _status;
    }

    public PubItem(HttpStatusCode statusCode, PubLinks _links) {
        this._links = _links;
        this._status = statusCode == null ? null : new PubStatus(statusCode);
    }

    public PubLinks get_links() {
        return _links;
    }

    public PubStatus get_status() {
        return _status;
    }

    public PubLink getLink(String rel) {
        return _links.getLink(rel);
    }
}
