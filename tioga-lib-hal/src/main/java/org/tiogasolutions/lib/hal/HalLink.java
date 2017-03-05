package org.tiogasolutions.lib.hal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.StringUtils;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.tiogasolutions.dev.common.EnvUtils.findProperty;

public class HalLink {

    public static boolean forceHttps = "true".equals(findProperty("hal_links_force_https", "false"));
    public static int httpsPort = Integer.valueOf(findProperty("hal_links_https_port", "443"));

    private final URI href;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String title;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final boolean templated;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String deprecation;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<HalCurie> curies;

    /**
     * 5.5.  name
     *
     * The "name" property is OPTIONAL.
     *
     * Its value MAY be used as a secondary key for selecting Link Objects
     * which share the same relation type.
     *
     * Current multiple links for the same relation type are not supported by this API
     */
    @JsonIgnore
    private final String name = null;

    /**
     * 5.3.  type
     *
     * The "type" property is OPTIONAL.
     *
     * Its value is a string used as a hint to indicate the media type
     * expected when dereferencing the target resource.
     *
     * Current not supported by this API.
     */
    @JsonIgnore
    private final String type = null;

    /** 5.6.  profile
     *
     * The "profile" property is OPTIONAL.
     *
     * Its value is a string which is a URI that hints about the profile (as
     * defined by [I-D.wilde-profile-link]) of the target resource.
     *
     * Current not supported by this API.
     */
    @JsonIgnore
    private final URI profile = null;

    /**
     * 5.8.  hreflang
     *
     * The "hreflang" property is OPTIONAL.
     *
     * Its value is a string and is intended for indicating the language of
     * the target resource (as defined by [RFC5988]).
     */
    @JsonIgnore
    private final String hreflang = null;

    /** Provided strictly to support @JsonInclude(JsonInclude.Include.NON_DEFAULT) */
    private HalLink() {
        this.href = null;
        this.title = null;
        this.templated = false;
        this.deprecation = null;
        this.curies = Collections.emptyList();
    }

    @JsonCreator
    protected HalLink(@JsonProperty("href") URI href,
                      @JsonProperty("title") String title,
                      @JsonProperty("templated") boolean templated,
                      @JsonProperty("deprecation") String deprecation,
                      @JsonProperty("curies") List<HalCurie> curies) {

        if (forceHttps && "http".equals(href.getScheme())) {
            int pos = href.toString().indexOf("/", 8);

            String newUri = "https://";
            if (StringUtils.isNotBlank(href.getUserInfo())) {
                newUri += href.getUserInfo();
                newUri += "@";
            }
            newUri += href.getHost();
            newUri += ":";
            newUri += httpsPort;

            String remainder = (pos < 0) ? "" : href.toString().substring(pos);
            newUri += remainder;

            this.href = URI.create(newUri);

        } else {
            this.href = href;
        }

        this.title = title;
        this.templated = templated;
        this.deprecation = deprecation;

        if (curies == null) curies = Collections.emptyList();
        this.curies = Collections.unmodifiableList(curies);
    }

    public URI getHref() {
        return href;
    }

    public String getTitle() {
        return title;
    }

    public boolean isTemplated() {
        return templated;
    }

    public String getDeprecation() {
        return deprecation;
    }

    public List<HalCurie> getCuries() {
        return curies;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public URI getProfile() {
        return profile;
    }

    public String getHreflang() {
        return hreflang;
    }

    public HalLink deprecation(URI deprecation) {
        return deprecation(deprecation.toString());
    }

    public HalLink deprecation(String deprecation) {
        return new HalLink(
                this.href,
                this.title,
                this.templated,
                deprecation,
                this.curies);
    }

    public HalLink templated() {
        return new HalLink(
                this.href,
                this.title,
                true,
                this.deprecation,
                this.curies);
    }

    public static HalLink create(URI href) {
        return new HalLink(href, null, false, null, null);
    }

    public static HalLink create(String href) {
        URI uri = URI.create(href);
        return new HalLink(uri, null, false, null, null);
    }

    public static HalLink create(URI href, String title) {
        return new HalLink(href, title, false, null, null);
    }

    public static HalLink create(String href, String title) {
        URI uri = URI.create(href);
        return new HalLink(uri, title, false, null, null);
    }
}