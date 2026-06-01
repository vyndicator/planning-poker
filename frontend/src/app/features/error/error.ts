import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { map } from 'rxjs';

@Component({
  selector: 'app-error',
  imports: [ButtonModule],
  template: `
    <div
      class="min-h-screen bg-surface-50 flex flex-col items-center justify-center gap-6 p-6 animate-fade-slide-up"
    >
      <div class="text-center flex flex-col items-center gap-3">
        <h1 class="text-2xl font-semibold">Something went wrong</h1>
        <p class="text-text-muted max-w-sm">{{ message() }}</p>
      </div>
      <p-button label="Back to lobby" icon="pi pi-home" (onClick)="router.navigate(['/'])" />
    </div>
  `,
})
export class ErrorComponent {
  protected readonly router = inject(Router);

  protected readonly message = toSignal(
    inject(ActivatedRoute).queryParams.pipe(
      map((p) => p['message'] ?? 'An unexpected error occurred.'),
    ),
  );
}
