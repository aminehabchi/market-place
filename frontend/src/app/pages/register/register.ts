import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UsersService } from '../../core/services/users-service';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  message = '';
  isSubmitting = false;
  errorMessage = signal({ msg: '', isthere: false })
  selectedAvatar: File | null = null;

  user = {
    email: '',
    name: '',
    password: '',
    confirmPassword: '',
    role: 'GUEST'
  };

  constructor(private loginService: UsersService) { }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length > 0 ? input.files[0] : null;
    if (file && file?.size > 1024 * 2048) {
      this.errorMessage.set({ msg: 'file size must not passe 2mb', isthere: true });
      return;
    } else if (file && file.type != 'image/png') {
      this.errorMessage.set({ msg: 'file type must be image', isthere: true });
      return;
    }
    this.selectedAvatar = file;
  }

  onSubmit(form: NgForm): void {
    if (form.invalid || this.isSubmitting) {
      return;
    }

    if (this.user.password !== this.user.confirmPassword) {
      this.errorMessage.set({ msg: 'Passwords do not match.', isthere: true });
      this.message = '';
      return;
    }

    this.isSubmitting = true;
    this.message = '';

    this.loginService.registerUserWithAvatar({
      email: this.user.email,
      name: this.user.name,
      password: this.user.password,
      role: this.user.role
    }, this.selectedAvatar).subscribe({
      next: (res) => {
        this.message = res.msg || 'Registration successful. You can login now.';
        this.isSubmitting = false;
        this.selectedAvatar = null;
        form.resetForm({
          email: '',
          name: '',
          password: '',
          confirmPassword: '',
          role: 'GUEST'
        });
        this.errorMessage.update((b) => {
          b.isthere = false;
          b.msg = '';
          return b;
        });

      },
      error: (err) => {
        this.errorMessage.set({
          msg: err?.error?.message || err?.error?.msg || 'Registration failed. Please try again.',
          isthere: true
        });
        this.isSubmitting = false;
      }
    });
  }
}
