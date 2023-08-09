package com.vocbuild.backend.service;

import com.vocbuild.backend.model.ElasticSearchModel;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;

public interface SearchServiceInterface {
    public void createOrUpdateDocument(@NonNull final String index, @NonNull final ElasticSearchModel model)
            throws IOException;

    public <T> List<T> searchDocument(
            @NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            @NonNull final Class<T> tClass) throws IOException;

    public <T> List<T> searchDocumentWithLimits(
            @NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            final int from,
            final int size,
            @NonNull final Class<T> tClass) throws IOException;

    public <T> long getTotal(@NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            @NonNull final Class<T> tClass) throws IOException;

}
