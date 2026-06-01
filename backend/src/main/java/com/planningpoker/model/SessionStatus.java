package com.planningpoker.model;

public enum SessionStatus {
    /** Session created, no issue selected yet */
    WAITING,
    /** Issue selected by Scrum Master, voting in progress */
    VOTING,
    /** Cards revealed by Scrum Master, awaiting finalization */
    REVEALED
}
