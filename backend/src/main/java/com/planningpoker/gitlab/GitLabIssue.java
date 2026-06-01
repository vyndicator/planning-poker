package com.planningpoker.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitLabIssue(
    int iid,
    String title,
    String description,
    @JsonProperty("web_url") String webUrl  // GitLab returns snake_case — map it here
) {}
