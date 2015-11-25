package org.tiogasolutions.lib.jaxrs.domain;

import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.LinkedList;
import java.util.List;

public class TiogaExceptionInfo {

  private final int status;
  private final String message;

  private final List<String> causes = new LinkedList<>();

  public TiogaExceptionInfo(int status, Throwable ex) {
    this.status = status;
    this.message = ExceptionUtils.getMessage(ex);

    List<? extends Throwable> allCauses = ExceptionUtils.getRootCauses(ex);

    for (Throwable cause : allCauses) {
      String msg = ExceptionUtils.getMessage(cause);
      causes.add(msg);
    }

    causes.remove(0); // Remove the original exception.
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public List<String> getCauses() {
    return causes;
  }
}
