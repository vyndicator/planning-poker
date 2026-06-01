import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { LobbyStore } from './lobby.store';

@Component({
  selector: 'app-lobby',
  imports: [
    ReactiveFormsModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
  ],
  providers: [LobbyStore],
  templateUrl: './lobby.html',
})
export class LobbyComponent {
  readonly store = inject(LobbyStore);
  private readonly fb = inject(FormBuilder);

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
