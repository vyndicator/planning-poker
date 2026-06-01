package com.planningpoker.dto;

import com.planningpoker.model.Role;

public record JoinResponse(String sessionId, String participantId, Role role) {}
