import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { SessionResourceService } from '../../../api';

export const sessionGuard: CanActivateFn = (route) => {
  const sessionId = route.paramMap.get('id')!;
  const api = inject(SessionResourceService);
  const router = inject(Router);

  return api.getSession({ sessionId }).pipe(
    map(() => true),
    catchError(() =>
      of(router.createUrlTree(['/error'], { queryParams: { message: 'Session not found or has ended.' } })),
    ),
  );
};
