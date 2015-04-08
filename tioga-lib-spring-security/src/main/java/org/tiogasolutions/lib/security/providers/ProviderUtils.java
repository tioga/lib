package org.tiogasolutions.lib.security.providers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class ProviderUtils {

  private ProviderUtils() {
  }

  public static boolean isGoogleAuthentication(UsernamePasswordAuthenticationToken authentication) {
    Object principal = authentication.getPrincipal();
    return principal != null && principal.equals("google-authentication");
  }
}
