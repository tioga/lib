package org.tiogasolutions.lib.security.providers;

import org.apache.commons.logging.*;
import org.tiogasolutions.dev.common.EqualsUtils;
import org.tiogasolutions.dev.domain.account.*;
import org.tiogasolutions.lib.security.CurrentUser;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.*;

public class CurrentUserSecurityProvider extends AbstractUserDetailsAuthenticationProvider {

  private static final Log log = LogFactory.getLog(CurrentUserSecurityProvider.class);

  private final CurrentUserStore store;

  public CurrentUserSecurityProvider(CurrentUserStore store) {
    this.store = store;
  }

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    if (EqualsUtils.objectsNotEqual(userDetails.getUsername(), authentication.getPrincipal())) {
      throw new BadCredentialsException(CurrentUser.INVALID_USER_NAME_OR_PASSWORD);
    }

    if (EqualsUtils.objectsNotEqual(userDetails.getPassword(), authentication.getCredentials())) {
      throw new BadCredentialsException(CurrentUser.INVALID_USER_NAME_OR_PASSWORD);
    }
  }

  @Override
  protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    if (ProviderUtils.isGoogleAuthentication(authentication)) {
      throw new UsernameNotFoundException("Goolge Authentication Required");
    }

    // We use an email address for the user name and
    // thus the disconnect between Account & UserDetails
    CurrentUserSource source = store.getCurrentUserSourceByName(userName);

    if (source == null) {
      throw new BadCredentialsException(CurrentUser.INVALID_USER_NAME_OR_PASSWORD);
    }

    return new CurrentUser(source);
  }
}
