import { Component, signal } from '@angular/core';
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
  // errorMessage = '';
  isSubmitting = false;
  errorMessage = signal({ msg: '', isthere: false })
  // errorMessage = { msg: '', isthere: false };

  user = {
    email: '',
    name: '',
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
      // this.errorMessage = 'Passwords do not match.';
      this.message = '';
      return;
    }

    this.isSubmitting = true;
    this.message = '';

    this.loginService.registerUser({
      email: this.user.email,
      name: this.user.name,
      password: this.user.password,
      role: this.user.role
    }).subscribe({
      next: (res) => {
        this.message = res.msg || 'Registration successful. You can login now.';
        this.isSubmitting = false;
        form.resetForm({
          email: '',
          name: '',
          password: '',
          confirmPassword: '',
          role: 'GUEST'
        });
        // this.errorMessage.isthere = false;
        this.errorMessage.update((b) => {
          b.isthere = false;
          b.msg = '';
          return b;
        });
        // this.errorMessage.msg = "";

      },
      error: (err) => {
        this.errorMessage.set({ msg: err.error.message, isthere: true });
        // console.error("whyyyyyyyy ", err.error.message);
        this.isSubmitting = false;
        // console.error(this.errorMessage.msg);
      }
    });
  }
}
