package com.twa.flights.api.provider.alpha.serializer;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.*;

import java.io.IOException;

public class JsonSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializer.class);
    private static final ObjectMapper OBJECT_MAPPER;

    private JsonSerializer() {
        // just to avoid create instances
    }

    static {
        OBJECT_MAPPER = new ObjectMapper().configure(MapperFeature.USE_GETTERS_AS_SETTERS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE).registerModule(new JavaTimeModule());
    }

    public static byte[] serialize(Object object) {
        byte[] compressedJson = null;
        try {
            compressedJson = OBJECT_MAPPER.writeValueAsString(object).getBytes();
        } catch (IOException e) {
            LOGGER.error("Error serializing object: {}", e.getMessage());
        }
        return compressedJson;
    }

    public static <T> T deserialize(byte[] raw, Class<T> reference) {
        if (raw == null)
            return null;

        T object = null;
        try {
            object = OBJECT_MAPPER.readValue(raw, reference);
        } catch (IOException e) {
            LOGGER.error("Can't deserialize object: {}", e.getMessage());
        }
        return object;
    }

}
