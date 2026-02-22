import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LoginService } from '../login/loginService';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  message = '';
  errorMessage = '';
  isSubmitting = false;
  user = {
    email: '',
    username: '',
    password: '',
    confirmPassword: '',
    role: 'GUEST'
  };

  constructor(private loginService: LoginService) { }

  onSubmit(form: NgForm): void {
    if (form.invalid || this.isSubmitting) {
      return;
    }

    if (this.user.password !== this.user.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      this.message = '';
      return;
    }

    this.isSubmitting = true;
    this.message = '';
    this.errorMessage = '';

    this.loginService.registerUser({
      email: this.user.email,
      username: this.user.username,
      password: this.user.password,
      role: this.user.role
    }).subscribe({
      next: (res) => {
        this.message = res.msg || 'Registration successful. You can login now.';
        this.isSubmitting = false;
        form.resetForm({
          email: '',
          username: '',
          password: '',
          confirmPassword: '',
          role: 'GUEST'
        });
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || err?.error?.msg || 'Registration failed. Please try again.';
        this.isSubmitting = false;
      }
    });
  }

}
