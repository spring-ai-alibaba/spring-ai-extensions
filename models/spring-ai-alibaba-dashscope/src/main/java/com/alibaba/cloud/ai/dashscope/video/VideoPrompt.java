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

package com.alibaba.cloud.ai.dashscope.video;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.ai.model.ModelRequest;

/**
 * Video prompt for DashScope video generation. Supports both traditional constructors
 * and builder pattern for flexible configuration.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class VideoPrompt implements ModelRequest<List<VideoMessage>> {

	private final List<VideoMessage> messages;

	private final VideoOptions options;

    /**
     * Create a video prompt with multiple messages and options.
     *
     * @param messages the list of video messages
     * @param options  the video generation options
     */
	public VideoPrompt(List<VideoMessage> messages, VideoOptions options) {

		this.messages = messages;
		this.options = options;
	}

	@Override
	public List<VideoMessage> getInstructions() {

		return this.messages;
	}

	@Override
	public VideoOptions getOptions() {

		return this.options;
    }

    /**
     * Create a new builder for constructing VideoPrompt instances.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating VideoPrompt instances with a fluent API.
     */
    public static class Builder {

        private List<VideoMessage> messages = new ArrayList<>();

        private VideoOptions options;

        private Builder() {
        }

        /**
         * Set the text content for video generation.
         * @param content the text content
         * @return this builder
         */
        public Builder content(String content) {
            this.messages = Collections.singletonList(new VideoMessage(content));
            return this;
        }

        /**
         * Add a single message.
         * @param message the video message
         * @return this builder
         */
        public Builder message(VideoMessage message) {
            if (this.messages.isEmpty()) {
                this.messages = new ArrayList<>();
            }
            this.messages.add(message);
            return this;
        }

        /**
         * Set the list of messages.
         * @param messages the list of video messages
         * @return this builder
         */
        public Builder messages(List<VideoMessage> messages) {
            this.messages = new ArrayList<>(messages);
            return this;
        }

        /**
         * Set the video generation options.
         * @param options the video options
         * @return this builder
         */
        public Builder options(VideoOptions options) {
            this.options = options;
            return this;
        }

        /**
         * Build the VideoPrompt instance.
         * @return a new VideoPrompt instance
         */
        public VideoPrompt build() {
            // If no messages were set but options were provided, create an empty message
            // This handles the case where prompt is in the options (e.g., DashScopeVideoOptions)
            if (this.messages.isEmpty() && this.options != null) {
                this.messages = Collections.singletonList(new VideoMessage(""));
            }
            return new VideoPrompt(this.messages, this.options);
		}

	}

}
