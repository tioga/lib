package org.tiogasolutions.lib.couchace.sequence;

import com.couchace.core.api.CouchServer;
import com.couchace.core.api.response.WriteResponse;
import org.tiogasolutions.lib.couchace.DefaultCouchStore;

public class SequenceStore extends DefaultCouchStore<Sequence> {

  public static final String SEQUENCE_DESIGN_NAME = "sequence";

  private final String databaseName;

  public SequenceStore(CouchServer couchServer, String databaseName) {
    super(couchServer, Sequence.class);
    this.databaseName = databaseName;
  }

  @Override
  public String getDesignName() {
    return SEQUENCE_DESIGN_NAME;
  }

  @Override
  public String getDatabaseName() {
    return databaseName;
  }

  public String getNextValue(SequenceType type) {
    Sequence sequence = super.getByDocumentId(type.getId());

    if (sequence == null) {
      // The entity doesn't exist, we need to create it.
      return create(type);
    }

    String nextValue = sequence.incrementValue();

    WriteResponse writeResponse = super.update(sequence);

    if (writeResponse.isCreated()) {
      return nextValue; // success
    } else {
      // We failed, retry for the next value.
      return getNextValue(type);
    }

  }

  private String create(SequenceType type) {
    Sequence sequence = new Sequence(type, "1");

    WriteResponse response = super.create(sequence);

    if (response.isCreated()) {
      return sequence.getLastValue();
    }

    // We were not able to create it, someone already did...
    return getNextValue(type);
  }

  public static final String designDocuments = "{\n" +
    "   \"_id\": \"_design/sequence\",\n" +
    "   \"_rev\": \"1-1889459c85063b18661870eae618733c\",\n" +
    "   \"language\": \"javascript\",\n" +
    "   \"views\": {\n" +
    "       \"all\": {\n" +
    "           \"map\": \"\\nfunction(doc) {\\n  if (doc.entityType == 'sequence') {\\n    emit(doc._id, null);\\n  }\\n}\"\n" +
    "       }\n" +
    "   }\n" +
    "}";
}
