package org.tiogasolutions.lib.couchace;

import com.couchace.core.api.CouchServer;
import com.couchace.core.api.CouchSetup;
import com.couchace.core.api.http.CouchHttpException;
import com.couchace.core.spi.http.CouchHttpClient;
import com.couchace.core.spi.json.CouchJsonStrategy;
import com.couchace.jackson.JacksonCouchJsonStrategy;
import com.couchace.jersey.JerseyCouchHttpClient;
import com.fasterxml.jackson.databind.Module;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;

public class DefaultCouchServer extends CouchServer {

  public DefaultCouchServer(CouchSetup couchSetup) {
    super(couchSetup);
    validateConnection(couchSetup);
  }

  public DefaultCouchServer() {
    this(new CouchSetup(getAppUrl())
        .setUserName(getAppUserName())
        .setPassword(getAppPassword())
        .setHttpClient(JerseyCouchHttpClient.class)
        .setJsonStrategy(new JacksonCouchJsonStrategy(
            new TiogaJacksonModule()
        )));
  }

  public DefaultCouchServer(Module[] modules) {
    this(new CouchSetup(getAppUrl())
        .setUserName(getAppUserName())
        .setPassword(getAppPassword())
        .setHttpClient(JerseyCouchHttpClient.class)
        .setJsonStrategy(new JacksonCouchJsonStrategy(modules)));
  }

  public DefaultCouchServer(CouchHttpClient couchHttpClient,
                            CouchJsonStrategy couchJsonStrategy) {

    this(new CouchSetup(getAppUrl())
        .setUserName(getAppUserName())
        .setPassword(getAppPassword())
        .setHttpClient(couchHttpClient)
        .setJsonStrategy(couchJsonStrategy));
  }

  public DefaultCouchServer(CouchHttpClient couchHttpClient,
                            CouchJsonStrategy couchJsonStrategy,
                            String url, String userName, String password) {

    this(new CouchSetup(url)
      .setUserName(userName)
      .setPassword(password)
      .setHttpClient(couchHttpClient)
      .setJsonStrategy(couchJsonStrategy));
  }

  public void validateConnection(CouchSetup couchSetup) {
    try {
      assertConnection();

    } catch (CouchHttpException e) {
      String msg = String.format("Unable to verify connection for %s at %s.", couchSetup.getUserName(), couchSetup.getUrl());
      throw new CouchHttpException(e.getHttpStatus(), msg);
    }
  }

  public static String getAppPassword() {
    String userName = getAppUserName();
    return EnvUtils.findProperty("app.couch.password", userName);
  }

  public static String getAppUserName() {
    return EnvUtils.findProperty("app.couch.userName", "app-user");
  }

  public static String getAppUrl() {
    return EnvUtils.findProperty("app.couch.url", "http://localhost:5984");
  }
}
