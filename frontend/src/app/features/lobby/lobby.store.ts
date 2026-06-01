import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { tapResponse } from '@ngrx/operators';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { Role, SessionResourceService } from '../../api';
import { storageKey } from '../session/stores/session.store';

interface LobbyState {
  loading: boolean;
  error: string | null;
}

interface JoinResponse {
  sessionId: string;
  participantId: string;
  role: Role;
}

interface CreateParams {
  scrumMasterName: string;
  gitlabProjectId: string;
  gitlabToken: string;
}

interface JoinParams {
  sessionId: string;
  name: string;
}

export const LobbyStore = signalStore(
  withState<LobbyState>({ loading: false, error: null }),

  withMethods((store, api = inject(SessionResourceService), router = inject(Router)) => ({
    createSession: rxMethod<CreateParams>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap(({ scrumMasterName, gitlabProjectId, gitlabToken }) =>
          api
            .createSession({
              createSessionRequest: { scrumMasterName, gitlabProjectId, gitlabToken },
            })
            .pipe(
              tapResponse({
                next: (res: JoinResponse) => {
                  localStorage.setItem(
                    storageKey(res.sessionId),
                    JSON.stringify({
                      participantId: res.participantId,
                      myRole: res.role,
                      myName: scrumMasterName,
                    }),
                  );
                  router.navigate(['/session', res.sessionId]);
                },
                error: () =>
                  patchState(store, {
                    loading: false,
                    error: 'Could not create session. Check your GitLab credentials.',
                  }),
              }),
            ),
        ),
      ),
    ),

    joinSession: rxMethod<JoinParams>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap(({ sessionId, name }) =>
          api.joinSession({ sessionId, joinRequest: { name } }).pipe(
            tapResponse({
              next: (res: JoinResponse) => {
                localStorage.setItem(
                  storageKey(res.sessionId),
                  JSON.stringify({
                    participantId: res.participantId,
                    myRole: res.role,
                    myName: name,
                  }),
                );
                router.navigate(['/session', res.sessionId]);
              },
              error: () => patchState(store, { loading: false, error: 'Session not found.' }),
            }),
          ),
        ),
      ),
    ),
  })),
);
