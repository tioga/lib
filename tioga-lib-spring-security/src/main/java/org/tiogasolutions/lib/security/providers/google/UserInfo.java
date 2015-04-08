package org.tiogasolutions.lib.security.providers.google;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfo {

  private final String id;
  private final String email;
  private final boolean emailVerified;
  private final String name;
  private final String givenName;
  private final String familyName;
  private final String link;
  private final String picture;
  private final String gender;

  @JsonCreator
  public UserInfo(@JsonProperty("id") String id,
                  @JsonProperty("email") String email,
                  @JsonProperty("verified_email") boolean verified_email,
                  @JsonProperty("name") String name,
                  @JsonProperty("given_name") String givenName,
                  @JsonProperty("family_name") String familyName,
                  @JsonProperty("link") String link,
                  @JsonProperty("picture") String picture,
                  @JsonProperty("gender") String gender) {

    this.id = id;
    this.email = email;
    this.emailVerified = verified_email;
    this.name = name;
    this.givenName = givenName;
    this.familyName = familyName;
    this.link = link;
    this.picture = picture;
    this.gender = gender;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public String getName() {
    return name;
  }

  public String getGivenName() {
    return givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public String getLink() {
    return link;
  }

  public String getPicture() {
    return picture;
  }

  public String getGender() {
    return gender;
  }
}
