package org.tiogasolutions.lib.joda.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.*;
import org.joda.time.*;
import org.joda.time.format.ISODateTimeFormat;

@Provider
public class JodaParamConverterProvider implements ParamConverterProvider {

  public JodaParamConverterProvider() {
    super();
  }

  @Override
  public <T> ParamConverter<T> getConverter(java.lang.Class<T> rawType, Type genericType, Annotation[] annotations) {

    if (LocalDate.class.equals(rawType)) {
      return new LocalDateParamConverter<T>(rawType);

    } else if (LocalTime.class.equals(rawType)) {
      return new LocalTimeParamConverter<T>(rawType);

    } else if (LocalDateTime.class.equals(rawType)) {
      return new LocalDateTimeParamConverter<T>(rawType);

    } else if (DateTime.class.equals(rawType)) {
      return new DateTimeParamConverter<T>(rawType);
    }

    return null;
  }

  public static class LocalDateParamConverter<T> implements ParamConverter<T> {
    private final Class<T> rawType;
    public LocalDateParamConverter(Class<T> rawType) { this.rawType = rawType; }
    @Override public T fromString(String value) { return (value == null) ? null : rawType.cast(new LocalDate(value)); }
    @Override public String toString(T value) {
      return ISODateTimeFormat.date().print(LocalDate.class.cast(value));
    }
  }

  public static class LocalTimeParamConverter<T> implements ParamConverter<T> {
    private final Class<T> rawType;
    public LocalTimeParamConverter(Class<T> rawType) { this.rawType = rawType; }
    @Override public T fromString(String value) {
      return (value == null) ? null : rawType.cast(new LocalTime(value));
    }
    @Override public String toString(T value) {
      return ISODateTimeFormat.time().print((LocalTime)value);
    }
  }

  public static class LocalDateTimeParamConverter<T> implements ParamConverter<T> {
    private final Class<T> rawType;
    public LocalDateTimeParamConverter(Class<T> rawType) { this.rawType = rawType; }
    @Override public T fromString(String value) { return (value == null) ? null : rawType.cast(new LocalDateTime(value)); }
    @Override public String toString(T value) {
      return ISODateTimeFormat.dateTime().print((LocalDateTime)value);
    }
  }

  public static class DateTimeParamConverter<T> implements ParamConverter<T> {
    private final Class<T> rawType;
    public DateTimeParamConverter(Class<T> rawType) { this.rawType = rawType;}
    @Override public T fromString(String value) {
      return (value == null) ? null : rawType.cast(new DateTime(value));
    }
    @Override public String toString(T value) {
      return ISODateTimeFormat.dateTime().print((DateTime)value);
    }
  }

}
