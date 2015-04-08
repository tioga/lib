package org.tiogasolutions.lib.couchace.sequence;

import com.couchace.annotations.CouchEntity;
import com.couchace.annotations.CouchId;
import com.couchace.annotations.CouchRevision;
import com.couchace.core.api.CouchException;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.*;

@CouchEntity("sequence")
public class Sequence {

  private String revision;
  private String sequenceId;
  private String lastValue = "";

  @JsonCreator
  private Sequence(@JacksonInject("sequenceId") String sequenceId,
                   @JacksonInject("revision") String revision,
                   @JsonProperty("lastValue") String lastValue) {

    this.sequenceId = sequenceId;
    this.revision = revision;
    this.lastValue = lastValue;
  }

  public Sequence(SequenceType sequenceType, String firstValue) {
    this.sequenceId = ExceptionUtils.assertNotNull(sequenceType, "sequenceType").getId();
    this.lastValue = ExceptionUtils.assertNotNull(firstValue, "firstValue");
  }

  @CouchRevision
  public String getRevision() {
    return revision;
  }

  @CouchId
  public String getSequenceId() {
    return sequenceId;
  }

  public String getLastValue() {
    return String.valueOf(lastValue);
  }

  public String incrementValue() {

    char[] charValue = lastValue.toCharArray();

    for (int i = 0; i < charValue.length; i++) {
      charValue[i] = incrementChar(charValue[i]);
      if (charValue[i] != '0') {
        return (lastValue = String.valueOf(charValue));
      }
    }

    return (lastValue = String.valueOf(charValue) + '0');
  }

  public static char incrementChar(char chr) {
    if (chr >= '0' && chr < '9') {
      return ++chr;
    } else if (chr == '9') {
      return 'a';
    } else if (chr >= 'a' && chr < 'z') {
      return ++chr;
    } else if (chr == 'z') {
      return 'A';
    } else if (chr >= 'A' && chr < 'Z') {
      return ++chr;
    } else if (chr == 'Z') {
      return '0';
    } else {
      String msg = String.format("Cannot increment char for %s.", chr);
      throw new CouchException(-1, msg);
    }
  }
}
