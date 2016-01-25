package com.idzivinskyi.util;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.joda.time.DateTime;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GsonBuilderHelper {
    public static GsonBuilder builder() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(DateTime.class, DATETIME_TYPE_ADAPTER)
                .registerTypeAdapter(LocalDate.class, LOCALDATE_TYPE_ADAPTER)
                .registerTypeAdapter(LocalDateTime.class, LOCALDATETIME_TYPE_ADAPTER)
                .registerTypeAdapter(java.time.LocalDate.class, JAVA_LOCALDATE_TYPE_ADAPTER)
                .registerTypeAdapter(java.time.LocalDateTime.class, JAVA_LOCALDATETIME_TYPE_ADAPTER)
                .registerTypeAdapter(Boolean.class, BOOLEAN_ADAPTER)
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY);
    }

    public static class OptionalTypeAdapter<E> extends TypeAdapter<Optional<E>> {

        public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                Class<T> rawType = (Class<T>) type.getRawType();
                if (rawType != Optional.class) {
                    return null;
                }
                final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
                final Type actualType = parameterizedType.getActualTypeArguments()[0];
                TypeToken<?> actualTokenType = TypeToken.get(actualType);
                final TypeAdapter<?> adapter = gson.getAdapter(actualTokenType);
                return new OptionalTypeAdapter(adapter);
            }
        };
        private final TypeAdapter<E> adapter;

        public OptionalTypeAdapter(TypeAdapter<E> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void write(JsonWriter out, Optional<E> value) throws IOException {
            if (value != null) {
                if (value.isPresent()) {
                    out.beginArray();
                    adapter.write(out, value.get());
                    out.endArray();
                } else {
                    out.beginArray();
                    out.nullValue();
                    out.endArray();
                }
            } else {
                out.nullValue();
            }
        }

        @Override
        public Optional<E> read(JsonReader in) throws IOException {
            Optional result = Optional.absent();
            JsonToken peek = in.peek();
            if (peek == JsonToken.BEGIN_ARRAY) {
                in.beginArray();
                peek = in.peek();
                if (peek != JsonToken.NULL) {
                    result = Optional.fromNullable(adapter.read(in));
                } else {
                    in.nextNull();
                }
                in.endArray();
            } else {
                if (peek != JsonToken.NULL) {
                    result = Optional.fromNullable(adapter.read(in));
                } else {
                    in.nextNull();
                }
            }
            return result;
        }

    }

    public static final TypeAdapter<Boolean> BOOLEAN_ADAPTER = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            Boolean result = null;
            JsonToken peek = in.peek();
            if (peek == JsonToken.BEGIN_ARRAY) {
                in.beginArray();
                peek = in.peek();
                if (peek == JsonToken.BOOLEAN) {
                    result = in.nextBoolean();
                } else {
                    in.nextNull();
                }
                in.endArray();
            } else if (peek == JsonToken.STRING) {
                result = Boolean.valueOf(in.nextString());
            } else {
                result = in.nextBoolean();
            }
            return result;
        }
    };

    public static final TypeAdapter<DateTime> DATETIME_TYPE_ADAPTER = new TypeAdapter<DateTime>() {
        @Override
        public void write(JsonWriter out, DateTime value) throws IOException {
            out.value(value == null ? null : value.toString());
        }

        @Override
        public DateTime read(JsonReader in) throws IOException {
            return DateTime.parse(in.nextString());
        }
    };

    public static final TypeAdapter<LocalDate> LOCALDATE_TYPE_ADAPTER = new TypeAdapter<LocalDate>() {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(value == null ? null : value.toString());
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString());
        }
    };

    public static final TypeAdapter<LocalDateTime> LOCALDATETIME_TYPE_ADAPTER = new TypeAdapter<LocalDateTime>() {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            out.value(value == null ? null : value.toString());
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString());
        }
    };

    public static final TypeAdapter<LocalDate> JAVA_LOCALDATE_TYPE_ADAPTER = new TypeAdapter<LocalDate>() {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(value == null ? null : value.toString());
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString());
        }
    };

    public static final TypeAdapter<LocalDateTime> JAVA_LOCALDATETIME_TYPE_ADAPTER = new TypeAdapter<LocalDateTime>() {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            out.value(value == null ? null : value.toString());
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString());
        }
    };

}
