package org.vocbuild.util;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Jolt, an open-source library, used for JSON to JSON transformation. Currently, it can be used for structural
 * transformation not data manipulation. It provides transformations such as shift, default, remove, sort and
 * cardinality.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslatorUtil {

    /**
     * @param inputPojo takes an object which is treated as input
     * @param specPath file-path in which transformation specification has been written
     * @param expectedObject takes an object which is the type expected
     * @return converted Object <T> T
     */

    public static <T> T translate(Object inputPojo, String specPath, Class<T> expectedObject)
            throws JsonProcessingException {
        Object inputJSON = JsonUtils.cloneJson(inputPojo);
        List<Object> chainSpecJSON = JsonUtils.classpathToList(specPath);
        Chainr chainr = Chainr.fromSpec(chainSpecJSON);
        Object transformedOutput = chainr.transform(inputJSON);
        String jsonString = JsonUtils.toJsonString(transformedOutput);
        return JSONUtil.convertStringToObject(jsonString, expectedObject);

    }

    /**
     * @param jsonString takes a String which is treated as input
     * @param specPath file-path in which transformation specification has been written
     * @param expectedObject takes an object which is the type expected
     * @return converted Object <T> T
     */
    public static <T> T translate(String jsonString, String specPath, Class<T> expectedObject)
            throws JsonProcessingException {
        Object inputJSON = JsonUtils.jsonToObject(jsonString);
        return translate(inputJSON, specPath, expectedObject);
    }
}
