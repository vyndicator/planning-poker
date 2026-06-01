import { signalStore, withState } from '@ngrx/signals';
import { GitLabIssue } from '../../../api';

interface GitLabState {
  issues: GitLabIssue[];
  currentIssue: GitLabIssue | null;
  loading: boolean;
  error: string | null;
}

const initialState: GitLabState = {
  issues: [],
  currentIssue: null,
  loading: false,
  error: null,
};

export const GitLabStore = signalStore(
  withState(initialState),
);
