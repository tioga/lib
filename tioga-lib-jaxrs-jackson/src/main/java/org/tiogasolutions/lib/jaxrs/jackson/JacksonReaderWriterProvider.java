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

package org.tiogasolutions.lib.jaxrs.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.domain.money.Money;

/**
 * Provided for Jersey applications that wish to use Jackson for marshaling with a
 * specific object mapper to and from JSON. Derived classes need only annotate the
 * class with @Service for spring and @Provider for Jersey.
 */
public abstract class JacksonReaderWriterProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

  private final ObjectMapper objectMapper;

  protected boolean supportsArrays;
  protected boolean supportsEnums;
  protected boolean supportsPrimitives;

  private final List<Class<?>> supportedTypes = new ArrayList<>();

  private final List<MediaType> mediaTypes = new ArrayList<>();


  public JacksonReaderWriterProvider(ObjectMapper objectMapper, MediaType mediaType) {
    this(objectMapper, Collections.singletonList(mediaType));
  }

  public JacksonReaderWriterProvider(ObjectMapper objectMapper, Collection<MediaType> mediaTypes) {
    this.objectMapper = objectMapper;

    this.mediaTypes.addAll(mediaTypes);

    supportsArrays = true;
    supportsEnums = true;
    supportsPrimitives = true;

    supportedTypes.add(java.lang.String.class);
    supportedTypes.add(Number.class);
    supportedTypes.add(Collection.class);
    supportedTypes.add(Boolean.class);
    supportedTypes.add(Character.class);

    supportedTypes.add(Money.class);

    supportedTypes.add(java.time.ZonedDateTime.class);
    supportedTypes.add(java.time.Duration.class);
    supportedTypes.add(java.time.Instant.class);
    supportedTypes.add(java.time.LocalDateTime.class);
    supportedTypes.add(java.time.LocalDate.class);
    supportedTypes.add(java.time.LocalTime.class);
    supportedTypes.add(java.time.Period.class);

    try {
      supportedTypes.add(Class.forName("org.joda.time.DateMidnight"));
      supportedTypes.add(Class.forName("org.joda.time.DateTime"));
      supportedTypes.add(Class.forName("org.joda.time.Duration"));
      supportedTypes.add(Class.forName("org.joda.time.Instant"));
      supportedTypes.add(Class.forName("org.joda.time.LocalDateTime"));
      supportedTypes.add(Class.forName("org.joda.time.LocalDate"));
      supportedTypes.add(Class.forName("org.joda.time.LocalTime"));
      supportedTypes.add(Class.forName("org.joda.time.Period"));
      supportedTypes.add(Class.forName("org.joda.time.ReadableDateTime"));
      supportedTypes.add(Class.forName("org.joda.time.ReadableInstant"));
      supportedTypes.add(Class.forName("org.joda.time.Interval"));

    } catch (ClassNotFoundException ignored) {
      // If Joda is not on the class path we can just ignore it.
      /* ignored */
    }
  }

  public JacksonReaderWriterProvider(ObjectMapper objectMapper, Collection<Class<?>> supportedTypes, Collection<MediaType> mediaTypes, boolean supportsArrays, boolean supportsEnums, boolean supportsPrimitives) {
    this.objectMapper = objectMapper;

    this.supportsArrays = supportsArrays;
    this.supportsEnums = supportsEnums;
    this.supportsPrimitives = supportsPrimitives;

    this.supportedTypes.addAll(supportedTypes);
  }

  public final void addSupportedType(Class type) {
    this.supportedTypes.add(type);
  }

  public final void addMediaType(MediaType type) {
    this.mediaTypes.add(type);
  }

  @Override
  public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
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
    return isSupported(type, mediaType);
  }

  @Override
  public final boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return isSupported(type, mediaType);
  }

  protected boolean isSupported(Class<?> type, MediaType mediaType) {
    if (mediaTypes.contains(mediaType) == false) {
      return false;
    }

    if (type.isArray() && supportsArrays == false) {
      return false;
    } else if (type.isPrimitive() && supportsPrimitives == false) {
      return false;
    } else if (type.isEnum() && supportsEnums == false) {
      return false;
    }

    if (type.isArray()) {
      type = type.getComponentType();
    }

    for (Class<?> supportedType : supportedTypes) {
      if (supportedType.isAssignableFrom(type)) {
        return true;
      }
    }

    return false;
  }
}
