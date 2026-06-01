import { Routes } from '@angular/router';
import { sessionGuard } from './features/session/guards/session.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/lobby/lobby').then(m => m.LobbyComponent),
  },
  {
    path: 'session/:id',
    loadComponent: () => import('./features/session/session-table').then(m => m.SessionTableComponent),
    canActivate: [sessionGuard],
  },
  { path: '**', redirectTo: '' },
];
