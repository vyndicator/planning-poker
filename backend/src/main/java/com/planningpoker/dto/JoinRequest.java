package com.planningpoker.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinRequest(@NotBlank String name) {}
