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
package com.alibaba.cloud.ai.dashscope.api;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.model.ModelOptionsUtils;

/**
 * Lets you specify the format of the returned content. Valid values: {"type": "text"} or
 * {"type": "json_object"}. When set to {"type": "json_object"}, a JSON string in standard
 * format is output. Params reference:
 * <a href= "https://help.aliyun.com/zh/model-studio/qwen-structured-output">qwen-structured-output</a>
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * @author guanxu
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashScopeResponseFormat {

	/**
	 * Parameters must be one of 'text', 'json_object' or 'json_schema'.
	 */
	@JsonProperty("type")
	private Type type = Type.TEXT;

    /**
     * Configuration for structured JSON output when {@link #type} is 'json_schema'.
     * This field is required if {@link #type} is 'json_schema'; otherwise, it should be null.
     */
    @JsonProperty("json_schema")
    private JsonSchemaConfig jsonScheme;

	public Type getType() {

		return this.type;
	}

	public void setType(Type type) {

		this.type = type;
	}

    public JsonSchemaConfig getJsonScheme() {

        return this.jsonScheme;
    }

    public void setJsonScheme(JsonSchemaConfig jsonScheme) {

        this.jsonScheme = jsonScheme;
    }

    public DashScopeResponseFormat() {
    }

    /**
     * Use Builder instead
     */
    @Deprecated(since = "1.1.2.0", forRemoval = true)
    public DashScopeResponseFormat(Type type) {

        this.type = type;
    }

	public static Builder builder() {

		return new Builder();
	}

	/**
	 * Builder for {@link DashScopeResponseFormat}.
	 */
	public static class Builder {

		private Type type = Type.TEXT;

        private JsonSchemaConfig jsonScheme;

		public Builder type(Type type) {

			this.type = type;
			return this;
		}

		public Builder jsonScheme(JsonSchemaConfig jsonScheme) {

			this.jsonScheme = jsonScheme;
			return this;
		}

		public DashScopeResponseFormat build() {

            DashScopeResponseFormat responseFormat = new DashScopeResponseFormat();
            responseFormat.setType(this.type);
            responseFormat.setJsonScheme(this.jsonScheme);
            return responseFormat;
		}

	}

	@Override
	public String toString() {
        return ModelOptionsUtils.toJsonString(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DashScopeResponseFormat that = (DashScopeResponseFormat) o;
		return Objects.equals(type, that.type) && Objects.equals(jsonScheme, that.jsonScheme);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.jsonScheme);
	}

	/**
	 * ResponseFormat type. Valid values: {"type": "text"}, {"type": "json_object"} or {"type": "json_schema"}.
	 */
	public enum Type {

		/**
		 * Generates a text response. (default)
		 */
		@JsonProperty("text")
		TEXT,

		/**
		 * Enables JSON mode, which guarantees the message the model generates is valid
		 * JSON string.
		 */
		@JsonProperty("json_object")
		JSON_OBJECT,

		/**
		 * Enables JSON mode, which guarantees the message the model generates is valid
		 * JSON string in the specified format.
		 */
        @JsonProperty("json_schema")
        JSON_SCHEMA

	}

    /**
     * The configuration for a JSON Schema-based structured output.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JsonSchemaConfig {

        /**
         * The schema name.
         */
        @JsonProperty("name")
        private String name;

        /**
         * The schema description.
         */
        @JsonProperty("description")
        private String description;

        /**
         * The JSON Schema object defining the output structure.
         */
        @JsonProperty("schema")
        private Object schema;

        /**
         * The strict mode to the JSON Schema. Default value is false.
         * <ul>
         *     <li>true(recommended): strict mode</li>
         *     <li>false(not recommended): non-strict mode</li>
         * </ul>
         */
        @JsonProperty("strict")
        private Boolean strict = false;

        /**
         * Gets the schema name.
         *
         * @return the schema name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Sets the schema name.
         *
         * @param name the schema name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the schema description.
         *
         * @return the schema description
         */
        public String getDescription() {
            return this.description;
        }

        /**
         * Sets the schema description.
         *
         * @param description the schema description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Gets the JSON Schema object defining the output structure.
         *
         * @return the schema object
         */
        public Object getSchema() {
            return this.schema;
        }

        /**
         * Sets the JSON Schema object defining the output structure.
         *
         * @param schema the JSON Schema object
         */
        public void setSchema(Object schema) {
            this.schema = schema;
        }

        /**
         * Gets the JSON Schema strict mode.
         *
         * @return true strict mode is enabled; false or null is disabled
         */
        public Boolean getStrict() {
            return this.strict;
        }

        /**
         * Sets the JSON Schema strict mode.
         *
         * @param strict true to enable strict mode (recommended), false to disable
         */
        public void setStrict(Boolean strict) {
            this.strict = strict;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            JsonSchemaConfig that = (JsonSchemaConfig) o;
            return Objects.equals(this.name, that.name) && Objects.equals(this.description, that.description)
                    && Objects.equals(this.schema, that.schema) && Objects.equals(this.strict, that.strict);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.description, this.schema, this.strict);
        }

        /**
         * Returns a new builder for constructing instances of {@link JsonSchemaConfig}.
         *
         * @return a new builder instance
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder class for constructing instances of {@link JsonSchemaConfig}.
         */
        public static class Builder {

            private String name;

            private String description;

            private Object schema;

            private Boolean strict = false;

            /**
             * Sets the schema name.
             *
             * @param name the schema name
             * @return this builder instance
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Sets the schema description.
             *
             * @param description the schema description
             * @return this builder instance
             */
            public Builder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * Sets the JSON Schema object.
             *
             * @param schema the JSON Schema object
             * @return this builder instance
             */
            public Builder schema(Object schema) {
                this.schema = schema;
                return this;
            }

            /**
             * Sets the strict mode to the JSON Schema.
             *
             * @param strict true to enable strict mode (recommended), false to disable
             * @return this builder instance
             */
            public Builder strict(Boolean strict) {
                this.strict = strict;
                return this;
            }

            /**
             * Builds a new instance of {@link JsonSchemaConfig}.
             *
             * @return a new instance of {@link JsonSchemaConfig}
             */
            public JsonSchemaConfig build() {
                JsonSchemaConfig jsonSchemaConfig = new JsonSchemaConfig();
                jsonSchemaConfig.setName(this.name);
                jsonSchemaConfig.setDescription(this.description);
                jsonSchemaConfig.setSchema(this.schema);
                jsonSchemaConfig.setStrict(this.strict);
                return jsonSchemaConfig;
            }
        }
    }
}
