/**
 * WebSocket event types — manually maintained since WebSocket contracts
 * are not part of the OpenAPI spec.
 *
 * These mirror the Java enums/records in:
 *   com.planningpoker.websocket.WsEventType
 *   com.planningpoker.websocket.WsEvent
 */

export type WsEventType =
  | 'PARTICIPANT_JOINED'
  | 'PARTICIPANT_LEFT'
  | 'ISSUE_SELECTED'
  | 'VOTE_CAST'
  | 'CARDS_REVEALED'
  | 'ROUND_RESET'
  | 'ROUND_FINALIZED'
  | 'SESSION_DELETED';

export interface WsEvent<T = unknown> {
  type: WsEventType;
  data: T;
}

// Typed payloads for each event — use these when you know the event type

export interface ParticipantJoinedData {
  id: string;
  name: string;
  role: 'SCRUM_MASTER' | 'DEVELOPER';
}

export interface ParticipantLeftData {
  participantId: string;
}

/** VOTE_CAST only reveals who voted, not the value (hidden until reveal) */
export interface VoteCastData {
  participantId: string;
  participantName: string;
}

export interface IssueSelectedData {
  issueIid: number;
}

export interface RoundFinalizedData {
  issueIid: number;
  finalValue: string;
}
