package org.tiogasolutions.lib.security;

import java.util.*;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.domain.account.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CurrentUser implements UserDetails {

  public static final String INVALID_USER_NAME_OR_PASSWORD = "Invalid user name or password";

  private final String username;
  private final String accountId;

  private final String password;

  private final Permissions permissions;
  private final AccountStatus accountStatus;

  public CurrentUser(CurrentUserSource source) {
    this.accountId = source.getAccountId();
    this.username = source.getUsername();

    this.password = source.getPassword();

    this.permissions = source.getPermissions();
    this.accountStatus = source.getAccountStatus();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    for (String roleType : permissions.getRoleTypes()) {
      if (StringUtils.isNotBlank(roleType)) {
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(roleType);
        authorities.add(grantedAuthority);
      }
    }
    return Collections.unmodifiableCollection(authorities);
  }

  @Override
  public String getPassword() {
    return password;
  }

  public String getAccountId() {
    return accountId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountStatus.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountStatus.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return accountStatus.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return accountStatus.isEnabled();
  }

  public boolean equals(Object object) {
    if (object instanceof CurrentUser) {
      CurrentUser that = (CurrentUser)object;
      return this.getUsername().equals(that.getUsername());
    }
    return false;
  }
}
