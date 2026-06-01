package com.planningpoker.dto;

import jakarta.validation.constraints.NotNull;

public record SelectIssueRequest(@NotNull Integer issueIid) {}
