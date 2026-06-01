package com.planningpoker.websocket;

public record WsEvent(WsEventType type, Object data) {}
