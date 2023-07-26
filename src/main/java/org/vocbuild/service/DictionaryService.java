package org.vocbuild.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpException;
import org.springframework.stereotype.Service;
import org.vocbuild.model.Definition;
import org.vocbuild.util.TranslatorUtil;

@Service
@Slf4j
public class DictionaryService {

    final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    final String JOLT_SPEC_DICTIONARY_RESPONSE_PATH = "/jolt-spec/response/Dictionary.json";

    final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public Definition getMeaning(@NonNull final String word) throws HttpException {
        Request request = new Request.Builder()
                .url(BASE_URL + word)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Unexpected response code: {}", response);
                throw new HttpException("Unsuccessful response: " + response.code());
            }
            String responseBody = response.body().string();
            log.info("Received response: {}", responseBody);

            return TranslatorUtil.translate(responseBody, JOLT_SPEC_DICTIONARY_RESPONSE_PATH,
                    Definition.class);
        } catch (IOException e) {
            throw new HttpException("Error making request: " + e.getMessage());
        }

    }
}
