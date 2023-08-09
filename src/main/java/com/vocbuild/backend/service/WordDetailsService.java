package com.vocbuild.backend.service;

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
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public WordDetails getWordDetails(@NonNull final String word) throws HttpException, IOException {
        List<SubtitleModel> responseModel = searchService.searchDocument(
                "subtitle", "text", word, SubtitleModel.class);

        return new WordDetails(getMeaning(word), responseModel);
    }

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

    public long getTotal(@NonNull final String word) throws IOException {
        return searchService.getTotal("subtitle", "text", word, SubtitleModel.class);
    }
}
