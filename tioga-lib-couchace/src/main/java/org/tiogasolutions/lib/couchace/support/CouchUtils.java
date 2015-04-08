package org.tiogasolutions.lib.couchace.support;

import com.couchace.core.api.*;
import com.couchace.core.api.response.*;
import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.id.IdGenerator;

public abstract class CouchUtils {

  private static final Log log = LogFactory.getLog(CouchUtils.class);

  private CouchUtils() {
  }

  public static void compactAndCleanAll(CouchDatabase couchDatabase, Collection<String> designNames) {
    compactDatabase(couchDatabase);
    compactDesigns(couchDatabase, designNames);
    cleanupViews(couchDatabase);
  }

  public static void compactDatabase(CouchDatabase couchDatabase) {
    WriteResponse response = couchDatabase.post().database("_compact").execute();
    String msg = String.format("Compaction of database failed: %s %s", response.getHttpStatus().getCode(), response.getHttpStatus().getReason());
    validateResponse(response, msg);
  }

  public static void compactDesigns(CouchDatabase couchDatabase, Collection<String> designNames) {

    for (String designName : designNames) {
      WriteResponse response = couchDatabase.post().database("_compact/" + designName).execute();
      String msg = String.format("Compaction of %s failed: %s %s", designName, response.getHttpStatus().getCode(), response.getHttpStatus().getCode());
      validateResponse(response, msg);
    }
  }

  public static void cleanupViews(CouchDatabase couchDatabase) {
    WriteResponse response = couchDatabase.post().database("_view_cleanup").execute();
    String msg = String.format("View cleanup failed: %s %s", response.getHttpStatus().getCode(), response.getHttpStatus().getCode());
    validateResponse(response, msg);
  }

  public static boolean createDatabase(CouchDatabase database) {
    return createDatabase(database, null, new ArrayList<>());
  }
  public static boolean createDatabase(CouchDatabase database, IdGenerator idGenerator, String...documentPaths) {
    return createDatabase(database, idGenerator, Arrays.asList(documentPaths));
  }
  public static boolean createDatabase(CouchDatabase database, IdGenerator idGenerator, Collection<String> documentPaths) {

    if (database.exists()) {
      log.info("The database "+database.getDatabaseName()+" already exists.");
      return false;
    }

    log.warn("Creating the database " + database.getDatabaseName());
    WriteResponse response = database.createDatabase();

    if (response.isCreated() == false) {
      String msg = String.format("Unable to create database: %s %s", response.getHttpStatus().getCode(), response.getHttpStatus().getReason());
      throw ApiException.internalServerError(msg);
    }

    for (String documentPath : documentPaths) {
      try {
        log.warn("Creating the document "+documentPath);

        String json = IoUtils.toString(CouchUtils.class.getResourceAsStream(documentPath));

        String hacked = json.replace(" ", "");
        if (hacked.contains("\"_id\":\"") == false) {
          response = database.put().document(idGenerator.newId(), json).execute();

        } else {
          int posA = hacked.indexOf("\"_id\":\"") + 7;
          int posB = hacked.indexOf("\"", posA);
          String id = hacked.substring(posA, posB);
          response = database.put().document(id, json).execute();
        }

        if (response.isCreated() == false) {
          String msg = String.format("Unable to put document %s: %s %s", documentPath, response.getHttpStatus().getCode(), response.getHttpStatus().getReason());
          throw ApiException.internalServerError(msg);
        }
      } catch (IOException ex) {
        throw ApiException.internalServerError("Unexpected exception reading couch document.", ex);
      }
    }

    return true;
  }

  public static void validateDesign(CouchDatabase database, Collection<String> designNames, String filePath, String fileSuffix) {
    for (String designName : designNames) {
      try {

        // Read the existing document from source.
        String fullPath = filePath + designName + fileSuffix;
        InputStream in = CouchUtils.class.getResourceAsStream(fullPath);

        if (in == null) {
          String msg = String.format("The resource \"%s\" was not found.", fullPath);
          throw new NullPointerException(msg);
        }

        String expectedDoc = IoUtils.toString(in);

        String docId = "_design/" + designName;
        GetDocumentResponse getResponse = database.get().document(docId).execute();

        if (getResponse.isNotFound()) {
          WriteResponse writeResponse = database.put().design(designName, expectedDoc).execute();

          if (writeResponse.isCreated() == false) {
            throw new CouchException(writeResponse.getHttpStatus(), "Unsuccessful in creating design document " + designName);
          }

        } else {

          String existingDoc = getResponse.getFirstDocument().getContent();

          existingDoc = normalizeDoc(existingDoc);
          expectedDoc = normalizeDoc(expectedDoc);

          if (existingDoc.equals(expectedDoc) == false) {
            String msg = String.format("The two documents (%s) do not match:\n%s\n%s", designName, existingDoc, expectedDoc);
            throw new CouchException(-1, msg);
          }
        }

      } catch (Throwable ex) {
        String msg = String.format("Exception validating design of %s.", designName);
        throw new CouchException(-1, msg, ex);
      }
    }
  }

  private static void validateResponse(WriteResponse response, String msg) {
    int code = response.getHttpStatus().getCode();
    if (code < 200 || code >= 300) {
      throw new CouchException(response.getHttpStatus(), msg);
    }
  }

  private static String normalizeDoc(String doc) {
    doc = doc.replaceAll(" ", "");
    doc = doc.replaceAll("\n", "");
    doc = doc.replaceAll("\r", "");
    doc = doc.replaceAll("\t", "");
    doc = strip("_rev", doc);
    doc = strip("language", doc);
    return doc;
  }

  private static String strip(String word, String text) {
    String key = "\""+word+"\":\"";

    int posA = text.indexOf(key);
    if (posA < 0) return text;

    int posB = text.indexOf("\"", posA+key.length());
    if (posB < 0) return text;

    String left = text.substring(0, posA);
    String right = text.substring(posB+1);

    if (left.endsWith(",") && right.startsWith(",")) {
      right = right.substring(1);
    }

    String retVal = left+right;
    return retVal.trim();
  }
}
