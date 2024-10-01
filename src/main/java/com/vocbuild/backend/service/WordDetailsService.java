package com.vocbuild.backend.service;

import com.vocbuild.backend.exceptions.ServerException;
import com.vocbuild.backend.exceptions.ValidationException;
import com.vocbuild.backend.exceptions.VocBuildHttpException;
import com.vocbuild.backend.model.Definition;
import com.vocbuild.backend.model.SubtitleModel;
import com.vocbuild.backend.model.WordDetails;
import jakarta.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.vocbuild.backend.util.TranslatorUtil;

@Service
@Slf4j
public class WordDetailsService {

    @Qualifier("OpenSearchService")
    @Autowired
    SearchServiceInterface searchService;

    @Value("${resource.filePath}")
    String filePath;

    final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    final String JOLT_SPEC_DICTIONARY_RESPONSE_PATH = "/jolt-spec/response/Dictionary.json";

    Set<String> words = new HashSet<>();

    @PostConstruct
    @Bean
    public void loadWords() throws IOException {

        File file = new File(filePath + "english.txt");

        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String word = sc.nextLine();

            Pattern p = Pattern.compile("[^a-zA-Z]");
            if(!p.matcher(word).find()) {
                words.add(word);
            }
        }
    }

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

    public void getWordCounts() {
        Map<String, Integer> map = new TreeMap<>();
        List<SubtitleModel> documents = null;
        try {
            documents = searchService.getAllDocuments("subtitle", SubtitleModel.class);

            for (SubtitleModel document : documents) {
                for (String str : document.getText().split("\\s+")) {
                    map.merge(str.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(), 1, Integer::sum);
                }
            }

            map.entrySet().removeIf(entry -> (!isExistingWord(entry.getKey())));
            BufferedWriter[] f = new BufferedWriter[26];

            for (char ch = 'a'; ch <= 'z'; ch++) {
                f[ch - 'a'] = new BufferedWriter(new FileWriter(filePath + "word-count/" + ch + ".txt"));
            }

            log.info("Total distinct words available are: {}", map.size());

            map.forEach((k, v) -> {
                try {
                    f[k.charAt(0) - 'a'].append(k)
                            .append(", ")
                            .append(String.valueOf(v))
                            .append("\n");
                } catch (IOException e) {
                    throw new ServerException(e);
                }
            });
            for (char ch = 'a'; ch <= 'z'; ch++) {
                f[ch - 'a'].close();
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    public boolean isExistingWord(final String word) {
        return words.contains(word);
    }

    public List<String> getSuggestions(@NonNull final String word) {
        try {
            File file = new File(filePath + "word-count/" + word.toLowerCase().charAt(0) + ".txt");

            Scanner sc = new Scanner(file);
            List<String> suggestions = new ArrayList<>();
            boolean flag = false;
            int count = 0;
            while (sc.hasNextLine() && count < 10) {
                String fileWord = sc.nextLine();
                if(fileWord.startsWith(word.toLowerCase())) {
                    flag = true;
                    suggestions.add(fileWord.split("\\s+")[0]);
                    count++;
                } else if(flag) {
                    break;
                }
            }

            return suggestions;
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }
}
