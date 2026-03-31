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
package com.alibaba.cloud.ai.reader.github;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author HeYQ
 * @since 1.0.0
 */
public class GitHubResource implements Resource {

	private static final Logger logger = LoggerFactory.getLogger(GitHubResource.class);

	private final InputStream inputStream;

	private final GHContent content;

	public GitHubResource(GitHub gitHub, String owner, String repo, @Nullable String branch, String path) {
		if (Objects.isNull(branch)) {
			branch = "main";
		}
		try {
			content = gitHub.getRepository(owner + "/" + repo).getFileContent(path, branch);
			Assert.isTrue(content.isFile(), "Path must be a file");
			inputStream = content.read();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public GitHubResource(GHContent content) {
		try {
			this.content = content;
			inputStream = content.read();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static GitHubResource getInstance(GHContent content) {
		return new GitHubResource(content);
	}

	public GHContent getText() {
		return content;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public URL getURL() throws IOException {
		throw new UnsupportedOperationException("GitHubResource does not expose a direct URL");
	}

	@Override
	public URI getURI() throws IOException {
		throw new UnsupportedOperationException("GitHubResource does not expose a direct URI");
	}

	@Override
	public File getFile() throws IOException {
		throw new UnsupportedOperationException("GitHubResource does not expose a local file");
	}

	@Override
	public long contentLength() throws IOException {
		return 0;
	}

	@Override
	public long lastModified() throws IOException {
		return 0;
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		throw new UnsupportedOperationException("GitHubResource does not support createRelative");
	}

	@Override
	public String getFilename() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

			private @Nullable String apiUrl;

			private @Nullable String gitHubToken;

			private @Nullable String gitHubTokenOrganization;

			private @Nullable GitHub gitHub;

			private @Nullable String owner;

			private @Nullable String repo;

			private @Nullable String branch;

			private @Nullable String path;

			public Builder apiUrl(@Nullable String apiUrl) {
				this.apiUrl = apiUrl;
				return this;
			}

			public Builder gitHubToken(@Nullable String gitHubToken) {
				this.gitHubToken = gitHubToken;
				return this;
			}

			public Builder gitHubTokenOrganization(@Nullable String gitHubTokenOrganization) {
				this.gitHubTokenOrganization = gitHubTokenOrganization;
				return this;
			}

			public Builder gitHub(@Nullable GitHub gitHub) {
				this.gitHub = gitHub;
				return this;
			}

			public Builder owner(@Nullable String owner) {
				this.owner = owner;
				return this;
			}

			public Builder repo(@Nullable String repo) {
				this.repo = repo;
				return this;
			}

			public Builder branch(@Nullable String branch) {
				this.branch = branch;
				return this;
			}

			public Builder path(@Nullable String path) {
				this.path = path;
				return this;
			}

			public GitHubResource build() {
				createGithub();
				return new GitHubResource(Objects.requireNonNull(gitHub, "GitHub must not be null"),
						Objects.requireNonNull(owner, "Owner must not be null"),
						Objects.requireNonNull(repo, "Repo must not be null"), branch,
						Objects.requireNonNull(path, "Path must not be null"));
			}

		public List<GitHubResource> buildBatch() {
			createGithub();
			return loadGitHubResources();
		}

		private void createGithub() {
			Assert.notNull(owner, "Owner must not be null");
			Assert.notNull(repo, "Repo must not be null");
			Assert.notNull(path, "Path must not be null");
			if (Objects.isNull(gitHub)) {
				Assert.notNull(gitHubToken, "GitHub token must not be null");
				GitHubBuilder gitHubBuilder = new GitHubBuilder();
				if (apiUrl != null) {
					gitHubBuilder.withEndpoint(apiUrl);
				}
				if (gitHubToken != null) {
					if (gitHubTokenOrganization == null) {
						gitHubBuilder.withOAuthToken(gitHubToken);
					}
					else {
						gitHubBuilder.withOAuthToken(gitHubToken, gitHubTokenOrganization);
					}
				}
				try {
					this.gitHub = gitHubBuilder.build();
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		}

			private List<GitHubResource> loadGitHubResources() {
				List<GitHubResource> gitHubResources = new ArrayList<>();
				GitHub currentGitHub = Objects.requireNonNull(gitHub, "GitHub must not be null");
				String currentOwner = Objects.requireNonNull(owner, "Owner must not be null");
				String currentRepo = Objects.requireNonNull(repo, "Repo must not be null");
				String currentPath = Objects.requireNonNull(path, "Path must not be null");
				try {
					currentGitHub.getRepository(currentOwner + "/" + currentRepo)
						.getDirectoryContent(currentPath, branch)
						.forEach(ghDirectoryContent -> Builder.scanDirectory(ghDirectoryContent, gitHubResources));
				}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
			return gitHubResources;
		}

		private static void scanDirectory(GHContent ghContent, List<GitHubResource> gitHubResources) {
			if (ghContent.isDirectory()) {
				try {
					ghContent.listDirectoryContent()
						.forEach(ghDirectoryContent -> Builder.scanDirectory(ghDirectoryContent, gitHubResources));
				}
				catch (IOException ioException) {
					logger.error("Failed to read directory from GitHub: {}", ghContent.getHtmlUrl(), ioException);
				}
			}
			else {
				GitHubResource gitHubResource = null;
				try {
					gitHubResource = GitHubResource.getInstance(ghContent);
				}
				catch (RuntimeException runtimeException) {
					logger.error("Failed to read document from GitHub: {}", ghContent.getHtmlUrl(), runtimeException);
				}
				if (gitHubResource != null) {
					gitHubResources.add(gitHubResource);
				}
			}
		}

	}

}
