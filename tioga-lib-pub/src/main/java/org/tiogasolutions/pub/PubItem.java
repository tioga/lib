package org.tiogasolutions.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

public class PubItem {

    @JsonIgnore
    private final PubStatus status;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final PubLinks _links;

    @JsonCreator
    public PubItem(@JsonProperty("status") PubStatus status,
                   @JsonProperty("_links") PubLinks _links) {

        this.status = (status != null) ? status : new PubStatus(HttpStatusCode.UNDEFINED);
        this._links = (_links != null) ? _links : PubLinks.empty();
    }

    public PubLinks get_links() {
        return _links;
    }

    public PubStatus getStatus() {
        return status;
    }

    public PubLink getLink(String rel) {
        return _links.getLink(rel);
    }

    public static PubItem create(PubStatus _status) {
        return new PubItem(_status, null);
    }

    public static PubItem create(int statusCode, PubLinks _links) {
        return new PubItem(new PubStatus(HttpStatusCode.findByCode(statusCode)), _links);
    }

    public static PubItem create(HttpStatusCode statusCode, PubLinks _links) {
        return new PubItem(new PubStatus(statusCode), _links);
    }
}
