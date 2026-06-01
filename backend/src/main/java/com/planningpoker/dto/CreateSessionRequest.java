package com.planningpoker.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSessionRequest(
    @NotBlank String scrumMasterName,
    @NotBlank String gitlabProjectId,
    @NotBlank String gitlabToken
) {}
