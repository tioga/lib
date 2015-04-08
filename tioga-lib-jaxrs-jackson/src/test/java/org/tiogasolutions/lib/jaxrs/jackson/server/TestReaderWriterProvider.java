package org.tiogasolutions.lib.jaxrs.jackson.server;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.dev.testing.domain.FreeBird;
import org.tiogasolutions.lib.jaxrs.jackson.JacksonReaderWriterProvider;

@Provider
public class TestReaderWriterProvider extends JacksonReaderWriterProvider {

  public TestReaderWriterProvider() {
    super(new TiogaJacksonObjectMapper(), MediaType.APPLICATION_JSON_TYPE);
    addSupportedType(FreeBird.class);
  }
}
