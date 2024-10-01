package com.vocbuild.backend.service;

import com.vocbuild.backend.exceptions.ServerException;
import com.vocbuild.backend.exceptions.VocBuildHttpException;
import com.vocbuild.backend.model.OMDBMetadata;
import com.vocbuild.backend.util.TranslatorUtil;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OMDBService {
    @Value("${omdb.url}")
    String baseUrl;

    @Value("${omdb.key}")
    String apiKey;

    final String JOLT_SPEC_OMDB_RESPONSE_PATH = "/jolt-spec/response/OMDB.json";

    final OkHttpClient client = new Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public String getMovieNameFromIMDBId(@NonNull final String imdbId) {
        Request request = new Request.Builder()
                .url(baseUrl + "?i=" + imdbId + "&apikey=" + apiKey)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Unexpected response code: {}", response);
                throw new VocBuildHttpException("Unsuccessful response: " + response.code());
            }
            String responseBody = response.body().string();
            log.info("Received response: {}", responseBody);

            return TranslatorUtil.translate(responseBody, JOLT_SPEC_OMDB_RESPONSE_PATH,
                    OMDBMetadata.class).getTitle();
        } catch (IOException e) {
            throw new ServerException("Error making request: " + e.getMessage());
        }
    }
}
