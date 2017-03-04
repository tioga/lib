package org.tiogasolutions.lib.jaxrs.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.pub.PubItem;
import org.tiogasolutions.pub.PubLinks;
import org.tiogasolutions.pub.PubResponse;

import javax.ws.rs.WebApplicationException;

public class TiogaExceptionInfo extends PubItem {

    @JsonCreator
    public TiogaExceptionInfo(@JsonProperty("_status") PubResponse _response,
                              @JsonProperty("_links") PubLinks _links) {

        super(_response, _links);
    }

    public TiogaExceptionInfo(ApiException ex) {
        super(toStatus(ex.getStatusCode()));
    }

    public TiogaExceptionInfo(WebApplicationException ex) {
        super(toStatus(ex.getResponse().getStatus()));
    }
}
