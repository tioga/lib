package org.tiogasolutions.lib.couchace.sequence;

public enum TestSequenceType implements SequenceType {

  test("test"),
  tinyUrl("tiny-url");

  public static final String ENTITY_TYPE = "sequence";

  private final String code;

  TestSequenceType(String code) {
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getId() {
    return "sequence-" + code;
  }

  @Override
  public String getEntityType() {
    return ENTITY_TYPE;
  }
}
