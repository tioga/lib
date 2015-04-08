package org.tiogasolutions.lib.thymeleaf;

import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;

import java.io.InputStream;

public class ClassPathResourceResolver implements IResourceResolver {

  public ClassPathResourceResolver() {
  }

  @Override
  public String getName() {
    return "CLASSPATH";
  }

  @Override
  public InputStream getResourceAsStream(final TemplateProcessingParameters templateProcessingParameters, String resourceName) {
    ExceptionUtils.assertNotNull(resourceName, "resourceName");
    if (resourceName.startsWith("classpath:") == false) {
      return null;
    }

    resourceName = resourceName.substring(10);

    InputStream is = getClass().getResourceAsStream(resourceName);
    if (is != null) return is;

    String msg = "The resource was not found on the classpath: " + resourceName;
    throw new IllegalArgumentException(msg);
  }
}
