package com.vocbuild.backend.service;

import com.vocbuild.backend.model.ElasticSearchModel;
import com.vocbuild.backend.model.SubtitleModel;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public interface SearchServiceInterface {
    void createOrUpdateDocument(@NonNull final String index, @NonNull final ElasticSearchModel model)
            throws IOException;

    <T> List<T> searchDocument(
            @NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            @NonNull final Class<T> tClass) throws IOException;

    <T> List<T> searchDocumentWithLimits(
            @NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            final int from,
            final int size,
            @NonNull final Class<T> tClass) throws IOException;

    <T> List<T> getAllDocuments(@NonNull String index, @NonNull Class<T> tClass)
            throws IOException;

    <T> long getTotal(@NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            @NonNull final Class<T> tClass) throws IOException;

    <T> List<T> searchDocumentWithWildcards(@NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            @NonNull final Class<T> tClass) throws IOException;
}
