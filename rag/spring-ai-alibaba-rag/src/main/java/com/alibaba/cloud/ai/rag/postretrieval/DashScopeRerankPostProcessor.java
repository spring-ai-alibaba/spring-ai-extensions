/*
 * Copyright 2023-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.rag.postretrieval;

import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import com.alibaba.cloud.ai.model.RerankModel;
import com.alibaba.cloud.ai.model.RerankRequest;
import com.alibaba.cloud.ai.model.RerankResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Re-ranking processing of the rerank model based on the dashscope api
 *
 * @author benym
 * @since 1.1.0.0-SNAPSHOT
 */
public class DashScopeRerankPostProcessor implements DocumentPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DashScopeRerankPostProcessor.class);

    private final RerankModel rerankModel;

    private final DashScopeRerankOptions rerankOptions;

    public DashScopeRerankPostProcessor(RerankModel rerankModel, DashScopeRerankOptions rerankOptions) {
        this.rerankModel = rerankModel;
        this.rerankOptions = rerankOptions;
    }

    @Override
    public List<Document> process(Query query, List<Document> documents) {
        try {
            List<Document> rerankDocuments = new ArrayList<>();
            if (Objects.nonNull(query) && StringUtils.hasText(query.text())) {
                RerankRequest rerankRequest = new RerankRequest(query.text(), documents, rerankOptions);
                RerankResponse rerankResponse = rerankModel.call(rerankRequest);
                Map<String, Document> docMap = documents.stream()
                        .collect(Collectors.toMap(Document::getId, Function.identity()));
                rerankResponse.getResults().forEach(res -> {
                    Document outputDocument = res.getOutput();
                    Document doc = docMap.get(outputDocument.getId());
                    if (doc != null) {
                        rerankDocuments.add(doc);
                    }
                });
            }
            return rerankDocuments;
        }
        catch (Exception e) {
            logger.error("rerank error in DashScopeRerankPostProcessor", e);
            return documents;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private RerankModel rerankModel;

        private DashScopeRerankOptions rerankOptions;

        public Builder rerankModel(RerankModel rerankModel) {
            this.rerankModel = rerankModel;
            return this;
        }

        public Builder rerankOptions(DashScopeRerankOptions rerankOptions) {
            this.rerankOptions = rerankOptions;
            return this;
        }

        @NotNull
        public DashScopeRerankPostProcessor build() {
            Assert.notNull(rerankModel, "rerankModel is required");
            Assert.notNull(rerankOptions, "rerankOptions is required");
            return new DashScopeRerankPostProcessor(rerankModel, rerankOptions);
        }
    }
}
