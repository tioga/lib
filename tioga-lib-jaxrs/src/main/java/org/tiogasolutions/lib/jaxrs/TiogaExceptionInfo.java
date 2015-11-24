package org.tiogasolutions.lib.jaxrs;

import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.LinkedList;
import java.util.List;

public class TiogaExceptionInfo {

  private final int status;
  private final String message;

  private final List<String> causes;

  public TiogaExceptionInfo(int status, Throwable ex) {
    this.status = status;
    this.message = ExceptionUtils.getMessage(ex);

    List<? extends Throwable> allCauses = ExceptionUtils.getRootCauses(ex);

    List<String> causes = new LinkedList<>();
    for (Throwable cause : allCauses) {
      String msg = ExceptionUtils.getMessage(cause);
      causes.add(msg);
    }

    causes.remove(0); // Remove the original exception.

    this.causes = (causes.isEmpty() ? null : causes);
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
