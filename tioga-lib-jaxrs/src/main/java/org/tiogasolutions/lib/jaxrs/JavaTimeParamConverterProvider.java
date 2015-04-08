package org.tiogasolutions.lib.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.*;
import javax.ws.rs.ext.*;
import org.tiogasolutions.dev.common.DateUtils;

@Provider
public class JavaTimeParamConverterProvider implements ParamConverterProvider {

  @Override
  public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {

    if (LocalDate.class.equals(rawType)) {
          return new LocalDateParamConverter<T>(rawType);

        } else if (LocalTime.class.equals(rawType)) {
          return new LocalTimeParamConverter<T>(rawType);

        } else if (LocalDateTime.class.equals(rawType)) {
          return new LocalDateTimeParamConverter<T>(rawType);

        } else if (ZonedDateTime.class.equals(rawType)) {
          return new ZonedDateTimeParamConverter<T>(rawType);

        }

        return null;
  }

  private static class LocalDateParamConverter<T> implements ParamConverter<T> {
    private final Class<T> rawType;
    private LocalDateParamConverter(Class<T> rawType) { this.rawType = rawType; }
    @Override public T fromString(String value) { return (value == null) ? null : rawType.cast(DateUtils.toLocalDate(value)); }
    @Override public String toString(T value) { return (value == null) ? "" : value.toString(); }
  }
  public class LocalDateTimeParamConverter<T> implements ParamConverter<T> {
    private final Class<T> rawType;
    public LocalDateTimeParamConverter(Class<T> rawType) { this.rawType = rawType; }
    @Override public T fromString(String value) { return (value == null) ? null : rawType.cast(DateUtils.toLocalDateTime(value)); }
    @Override public String toString(T value) { return (value == null) ? "" : value.toString(); }
  }
  public class LocalTimeParamConverter<T> implements ParamConverter<T> {
    private final Class<T> rawType;
    public LocalTimeParamConverter(Class<T> rawType) { this.rawType = rawType; }
    @Override public T fromString(String value) { return (value == null) ? null : rawType.cast(DateUtils.toLocalTime(value)); }
    @Override public String toString(T value) { return (value == null) ? "" : value.toString(); }
  }
  public class ZonedDateTimeParamConverter<T> implements ParamConverter<T> {
    private final Class<T> rawType;
    public ZonedDateTimeParamConverter(Class<T> rawType) { this.rawType = rawType; }
    @Override public T fromString(String value) { return (value == null) ? null : rawType.cast(DateUtils.toZonedDateTime(value)); }
    @Override public String toString(T value) { return (value == null) ? "" : value.toString(); }
  }
}
