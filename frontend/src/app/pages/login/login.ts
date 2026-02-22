import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { LoginService } from './loginService';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  message = '';
  errorMessage = '';
  isSubmitting = false;
  user = {
    identification: '',
    password: ''
  };

  constructor(
    private loginService: LoginService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: object
  ) { }

  onSubmit(form: NgForm): void {
    if (form.invalid || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.message = '';
    this.errorMessage = '';

    this.loginService.loginUser(this.user).subscribe({
      next: (res) => {
        this.message = res.message || 'Login successful.';
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('token', res.token);
          localStorage.setItem('role', res.role);
        }

        form.resetForm({ identification: '', password: '' });
        this.isSubmitting = false;
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || err?.error?.msg || 'Login failed. Please verify your credentials.';
        this.isSubmitting = false;
      }
    });
  }
}
