package com.planningpoker.model;

import java.util.List;

public record Round(Integer issueIid, List<Vote> votes, CardValue finalValue) {}
