import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { PasswordModule } from 'primeng/password';
import { ToastModule } from 'primeng/toast';
import { map } from 'rxjs';
import { LobbyStore } from './lobby.store';

@Component({
  selector: 'app-lobby',
  imports: [
    ReactiveFormsModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    MessageModule,
    ToastModule,
  ],
  providers: [LobbyStore],
  templateUrl: './lobby.html',
})
export class LobbyComponent {
  readonly store = inject(LobbyStore);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  readonly sessionError = toSignal(this.route.queryParams.pipe(map((p) => p['error'] ?? null)));

  readonly createForm = this.fb.nonNullable.group({
    scrumMasterName: ['', Validators.required],
    gitlabProjectId: ['', Validators.required],
    gitlabToken: ['', Validators.required],
  });

  readonly joinForm = this.fb.nonNullable.group({
    sessionId: ['', Validators.required],
    name: ['', Validators.required],
  });

  submitCreate(): void {
    this.createForm.markAllAsTouched();
    if (this.createForm.invalid) return;
    this.store.createSession(this.createForm.getRawValue());
  }

  submitJoin(): void {
    this.joinForm.markAllAsTouched();
    if (this.joinForm.invalid) return;
    this.store.joinSession(this.joinForm.getRawValue());
  }
}
