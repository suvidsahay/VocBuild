package com.vocbuild.backend.service;

import com.vocbuild.backend.exceptions.ErrorCode;
import com.vocbuild.backend.exceptions.ServerException;
import com.vocbuild.backend.exceptions.ValidationException;
import com.vocbuild.backend.exceptions.VocBuildHttpException;
import com.vocbuild.backend.model.Definition;
import com.vocbuild.backend.model.SubtitleModel;
import com.vocbuild.backend.model.WordDetails;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.vocbuild.backend.util.TranslatorUtil;

@Service
@Slf4j
public class WordDetailsService {

    @Qualifier("OpenSearchService")
    @Autowired
    SearchServiceInterface searchService;

    final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    final String JOLT_SPEC_DICTIONARY_RESPONSE_PATH = "/jolt-spec/response/Dictionary.json";

    final OkHttpClient client = new Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public WordDetails getWordDetails(@NonNull final String word) {
        List<SubtitleModel> responseModel = null;
        try {
            responseModel = searchService.searchDocument(
                    "subtitle", "text", word, SubtitleModel.class);
        } catch (IOException e) {
            throw new ServerException(e);
        }

        return new WordDetails(getMeaning(word), responseModel);
    }

    public Definition getMeaning(@NonNull final String word) {
        Request request = new Request.Builder()
                .url(BASE_URL + word)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Unexpected response code: {}", response);
                if(response.code() == HttpStatus.NOT_FOUND.value()) {
                    throw new ValidationException("The given word " + word + " doesn't exist. Please check your input and try again");
                }
                throw new VocBuildHttpException("Unsuccessful response: " + response.code());
            }
            String responseBody = response.body().string();
            log.info("Received response: {}", responseBody);

            return TranslatorUtil.translate(responseBody, JOLT_SPEC_DICTIONARY_RESPONSE_PATH,
                    Definition.class);
        } catch (IOException e) {
            throw new ServerException("Error making request: " + e.getMessage());
        }
    }

    public long getTotal(@NonNull final String word) {
        try {
            return searchService.getTotal("subtitle", "text", word, SubtitleModel.class);
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }
}
