package org.tiogasolutions.lib.couchace;

import com.couchace.core.api.CouchDatabase;
import com.couchace.core.api.CouchException;
import com.couchace.core.api.CouchServer;
import com.couchace.core.api.meta.AnnotationCouchMetaBuilder;
import com.couchace.core.api.meta.CouchEntityMeta;
import com.couchace.core.api.query.CouchViewQuery;
import com.couchace.core.api.request.GetEntityRequest;
import com.couchace.core.api.response.GetEntityResponse;
import com.couchace.core.api.response.HeadResponse;
import com.couchace.core.api.response.WriteResponse;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.lang.reflect.Field;
import java.util.*;

public abstract class DefaultCouchStore<T>{

  private final Class<T> entityType;
  private final CouchServer couchServer;

  private final Map<String,CouchDatabase> databaseMap = new HashMap<>();
  private final Map<String,Long> databaseAccessMap = new HashMap<>();

  public abstract String getDesignName();
  public abstract String getDatabaseName();

  public DefaultCouchStore(CouchServer couchServer, Class<T> entityType) {
    this.couchServer = ExceptionUtils.assertNotNull(couchServer, "couchServer");
    this.entityType = ExceptionUtils.assertNotNull(entityType, "entityType");
  }

  public CouchDatabase getDatabase() {

    String databaseName = getDatabaseName();

    if (databaseMap.containsKey(databaseName) == false) {
      CouchDatabase database = couchServer.database(databaseName);
      if (database.exists() == false) {
        createDatabase(database);
      }
      databaseMap.put(databaseName, database);
    }

    databaseAccessMap.put(databaseName, System.currentTimeMillis());
    CouchDatabase database = databaseMap.get(databaseName);

    expireCachedDatabases();

    return database;
  }

  public void createDatabase(CouchDatabase database) {
    String msg = String.format("Creation of the database \"%s\" is not supported.", getDatabaseName());
    throw new UnsupportedOperationException(msg);
  }

  public void expireCachedDatabases() {
    for (String dbName : databaseAccessMap.keySet()) {
      long accessedAt = databaseAccessMap.get(dbName);
      if (System.currentTimeMillis() - accessedAt > 1000 * 60) {
        databaseMap.remove(dbName);
        databaseAccessMap.remove(dbName);
      }
    }
  }

  public CouchServer getCouchServer() {
    return couchServer;
  }

  public Map<String, CouchDatabase> getDatabaseMap() {
    return Collections.unmodifiableMap(databaseMap);
  }

  public Map<String, Long> getDatabaseAccessMap() {
    return Collections.unmodifiableMap(databaseAccessMap);
  }

  public WriteResponse update(T entity) {
    WriteResponse response = getDatabase().put().entity(entity).execute();

    if (response.isCreated() == false) {
      java.lang.String msg = String.format("Unexpected DB response %s: %s\n", response.getHttpStatus(), response.getErrorContent());
      throw new CouchException(response.getHttpStatus(), msg);
    }

    injectVersion(entity, response);

    return response;
  }

  public T getByDocumentId(String documentId) {
    GetEntityRequest<T> request = getDatabase().get().entity(entityType, documentId);
    GetEntityResponse<T> response = request.execute();
    return response.isEmpty() ? null : response.getFirstEntity();
  }

  public HeadResponse headByDocumentId(String documentId) {
    return getDatabase().head().id(documentId).execute();
  }

  public WriteResponse delete(T entity) {
    WriteResponse deleteResponse = getDatabase().delete().entity(entity).execute();
    return validateDelete(deleteResponse);
  }

  public WriteResponse deleteByDocumentId(String documentId, String revision) {
    WriteResponse deleteResponse = getDatabase().delete().document(documentId, revision).execute();
    return validateDelete(deleteResponse);
  }

  private WriteResponse validateDelete(WriteResponse deleteResponse) {
    if (deleteResponse.isOk() == false) {
      String msg = String.format("Unexpected DB response %s: %s\n", deleteResponse.getHttpStatus(), deleteResponse.getErrorContent());
      throw new CouchException(deleteResponse.getHttpStatus(), msg);
    }
    return deleteResponse;
  }

  public WriteResponse create(T entity) {
    WriteResponse putResponse = getDatabase().put().entity(entity).execute();

    if (putResponse.isCreated() == false) {
      String msg = String.format("Unexpected DB response %s: %s\n", putResponse.getHttpStatus(), putResponse.getErrorContent());
      throw new CouchException(putResponse.getHttpStatus(), msg);
    }

    injectVersion(entity, putResponse);

    return putResponse;
  }

  protected List<T> getEntities(String viewName, String... keyValues) {
    return getEntities(viewName, (Object[])keyValues);
  }

  protected List<T> getEntities(String viewName, Object[] keyValues) {
    return getEntities(viewName, Arrays.asList(keyValues));
  }

  protected List<T> getEntities(String viewName, Collection<?> keyValues) {
    GetEntityResponse<T> response = getEntityResponse(getDesignName(), viewName, keyValues);
    return response.getEntityList();
  }

  protected GetEntityResponse<T> getEntityResponse(String designName, String viewName, Collection<?> keyValues) {

    CouchViewQuery couchViewQuery = CouchViewQuery
        .builder(designName, viewName)
        .key(keyValues.toArray())
        .includeDocs(true)
        .build();

    GetEntityResponse<T> response = getDatabase().get().entity(entityType, couchViewQuery).execute();

    if (response.isNotFound()) {
      String msg = String.format("The view \"_design/%s:%s\" does not exist.", designName, viewName);
      throw new CouchException(response.getHttpStatus(), msg);

    } else if (response.isOk() == false) {
      throw new CouchException(response.getHttpStatus());
    }

    return response;
  }

  protected void injectVersion(T entity, WriteResponse response) {
    String version = response.getDocumentRevision();

    CouchEntityMeta<T> meta = new AnnotationCouchMetaBuilder().buildEntityMeta(entityType);
    String property = meta.getRevisionName();
    Field field = ReflectUtils.getField(entity.getClass(), property);
    ReflectUtils.setPropertyValue(entity, field, version);
  }

  public Class<T> getEntityType() {
    return entityType;
  }
}
