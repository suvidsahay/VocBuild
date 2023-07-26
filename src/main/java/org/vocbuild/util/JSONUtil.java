package org.vocbuild.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * JSONUtils is a utility class which provides methods to convert/map JSON to String and vice-versa.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JSONUtil {

    private static final ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * @param object takes an object which is treated as input
     * @return converted JSON string
     */
    public static String toJsonString(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * @param jsonString takes a String which is treated as input
     * @param expectedObject takes an object which is the type expected
     * @return converted Object <T> T
     */
    public static <T> T convertStringToObject(String jsonString, Class<T> expectedObject)
            throws JsonProcessingException {
        return mapper.readValue(jsonString, expectedObject);
    }

    /**
     * @param object takes an object which is treated as input
     * @return converted Map<String, String>
     */
    public static Map<String, Object> convertObjectToMap(Object object, SimpleDateFormat dateFormat) {
        mapper.setDateFormat(dateFormat);
        return mapper.convertValue(object, Map.class);
    }

}
