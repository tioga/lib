/*
 * Copyright 2012 Jacob D Parr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tiogasolutions.lib.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Provided for Jersey applications that wish to use Jackson for marshaling with a
 * specific object mapper to and from JSON. Derived classes need only annotate the
 * class with @Service for spring and @Provider for Jersey.
 */
public abstract class TiogaReaderWriterProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

  private final ObjectMapper objectMapper;

  public TiogaReaderWriterProvider() {
    this.objectMapper = new TiogaJacksonObjectMapper();
  }

  public TiogaReaderWriterProvider(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1; // deprecated in JAX-RS 2.0
  }

  @Override
  public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> map, InputStream stream) throws IOException, WebApplicationException {
    String json = IoUtils.toString(stream);
    return objectMapper.readValue(json, type);
  }

  @Override
  public void writeTo(T value, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> map, OutputStream stream) throws IOException, WebApplicationException {
    String json = objectMapper.writeValueAsString(value);
    stream.write(json.getBytes());
  }

  @Override
  public final boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public final boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return true;
  }
}
