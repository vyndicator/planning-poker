package com.planningpoker.dto;

import com.planningpoker.model.CardValue;
import jakarta.validation.constraints.NotNull;

public record VoteRequest(@NotNull CardValue value) {}
