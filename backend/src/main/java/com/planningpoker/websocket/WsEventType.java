package com.planningpoker.websocket;

public enum WsEventType {
    PARTICIPANT_JOINED,
    PARTICIPANT_LEFT,
    ISSUE_SELECTED,
    VOTE_CAST,
    CARDS_REVEALED,
    ROUND_RESET,
    ROUND_FINALIZED,
    SESSION_DELETED
}
