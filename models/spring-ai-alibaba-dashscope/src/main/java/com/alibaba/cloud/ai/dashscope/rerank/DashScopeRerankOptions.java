/*
 * Copyright 2024-2026 the original author or authors.
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

package com.alibaba.cloud.ai.dashscope.rerank;

import com.alibaba.cloud.ai.model.RerankOptions;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * DashScope rerank auto-configuration properties.
 *
 * @author yuanci.ytb
 * @author xuguan
 * @since 1.0.0-M2
 */
public class DashScopeRerankOptions implements RerankOptions {

    /**
     * ID of the model to use.
     */
    private final String model;

    /**
     * return top n best relevant docs for query
     */
    private final Integer topN;

    /**
     * if need to return original document
     */
    private final Boolean returnDocuments;

    private final @Nullable String instruct;

    protected DashScopeRerankOptions(
            @Nullable String model,
            @Nullable Integer topN,
            @Nullable Boolean returnDocuments) {
        this(model, topN, returnDocuments, null);
    }

    private DashScopeRerankOptions(@Nullable String model, @Nullable Integer topN,
            @Nullable Boolean returnDocuments, @Nullable String instruct) {
        this.instruct = instruct;
        this.model = model != null ? model : "qwen3-rerank";
        this.topN = topN != null ? topN : 3;
        this.returnDocuments = returnDocuments != null ? returnDocuments : Boolean.FALSE;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public Integer getTopN() {
        return topN;
    }

    public Boolean getReturnDocuments() {
        return returnDocuments;
    }

    public @Nullable String getInstruct() {
        return this.instruct;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashScopeRerankOptions that = (DashScopeRerankOptions) o;
        return Objects.equals(this.model, that.model) && Objects.equals(this.topN, that.topN)
                && Objects.equals(this.returnDocuments, that.returnDocuments)
                && Objects.equals(this.instruct, that.instruct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.model, this.topN, this.returnDocuments, this.instruct);
    }

    @Override
    public String toString() {
        return "DashScopeRerankOptions{" + "model='" + this.model + '\'' + ", topN=" + this.topN + ", returnDocuments="
                + this.returnDocuments + '}';
    }

    public static final class Builder extends AbstractBuilder<DashScopeRerankOptions, Builder> {

        private Builder() {
        }

        public Builder from(DashScopeRerankOptions fromOptions) {
            this.model = fromOptions.getModel();
            this.topN = fromOptions.getTopN();
            this.returnDocuments = fromOptions.getReturnDocuments();
            this.instruct = fromOptions.getInstruct();
            return this;
        }

        public Builder merge(@Nullable RerankOptions from) {
            if (from == null) {
                return this;
            }
            if (from.getModel() != null) {
                this.model = from.getModel();
            }
            if (from.getTopN() != null) {
                this.topN = from.getTopN();
            }
            if (from instanceof DashScopeRerankOptions castFrom) {
                if (castFrom.getInstruct() != null) {
                    this.instruct = castFrom.getInstruct();
                }
                if (castFrom.getReturnDocuments() != null) {
                    this.returnDocuments = castFrom.getReturnDocuments();
                }
            }
            return this;
        }

        public DashScopeRerankOptions build() {
            return new DashScopeRerankOptions(this.model, this.topN, this.returnDocuments, this.instruct);
        }

    }

    protected abstract static class AbstractBuilder<O extends DashScopeRerankOptions, B extends AbstractBuilder<O, B>> {

        protected @Nullable String model;

        protected @Nullable Integer topN;

        protected @Nullable Boolean returnDocuments;

        protected @Nullable String instruct;

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }

        public B model(@Nullable String model) {
            this.model = model;
            return self();
        }

        public B topN(@Nullable Integer topN) {
            this.topN = topN;
            return self();
        }

        public B returnDocuments(@Nullable Boolean returnDocuments) {
            this.returnDocuments = returnDocuments;
            return self();
        }

        public B instruct(@Nullable String instruct) {
            this.instruct = instruct;
            return self();
        }

        public abstract O build();

    }

}
