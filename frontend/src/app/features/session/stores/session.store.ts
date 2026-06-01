import { computed } from '@angular/core';
import { signalStore, withComputed, withHooks, withState } from '@ngrx/signals';
import { Participant, Role, Session } from '../../../api';

export const storageKey = (sessionId: string) => `pp_session_${sessionId}`;

interface SessionState {
  sessionId: string | null;
  participantId: string | null;
  myRole: Role | null;
  myName: string | null;
  session: Session | null;
  wsConnected: boolean;
  allVotedNotified: boolean;
}

const initialState: SessionState = {
  sessionId: null,
  participantId: null,
  myRole: null,
  myName: null,
  session: null,
  wsConnected: false,
  allVotedNotified: false,
};

export const SessionStore = signalStore(
  withState(initialState),

  withComputed(({ participantId, myRole, session }) => ({
    isScrumMaster: computed(() => myRole() === Role.SCRUM_MASTER),

    participants: computed(() => session()?.participants ?? []),

    votes: computed(() => session()?.votes ?? {}),

    status: computed(() => session()?.status ?? null),

    currentIssueIid: computed(() => session()?.currentIssueIid ?? null),

    completedRounds: computed(() => session()?.completedRounds ?? []),

    hasVoted: computed(() => {
      const pid = participantId();
      const votes = session()?.votes ?? {};
      return pid != null && pid in votes;
    }),

    allVoted: computed(() => {
      const participants = session()?.participants ?? [];
      const votes = session()?.votes ?? {};
      return (
        participants.length > 0 &&
        participants.every((p: Participant) => p.id != null && p.id in votes)
      );
    }),
  })),

  withHooks({
    onInit() {},
    onDestroy() {},
  }),
);
