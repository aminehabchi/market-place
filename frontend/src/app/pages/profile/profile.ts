import { HttpClient } from '@angular/common/http';
import { Component, OnInit, signal } from '@angular/core';
import { Me, UpdateProfile, UsersService } from '../../core/services/users-service';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { PLATFORM_ID, Inject } from '@angular/core';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  userProfile = signal<Me | null>(null);
  isLoading = signal(true);
  isEditing = signal(false);
  isSubmitting = signal(false);
  errorMessage = signal('');
  successMessage = signal('');
  editFormName = signal('');
  editFormEmail = signal('');
  editFormAvatarUrl = signal('');

  constructor(
    private userService: UsersService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: object
  ) { }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.userService.meUser().subscribe({
      next: (res) => {
        this.userProfile.set(res);
        console.log(this.userProfile());

        this.editFormName.set(res.username);
        this.editFormEmail.set(res.email);
        this.editFormAvatarUrl.set(res.avatarUrl || '');

        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set('Failed to load profile. Please try again.');
        this.isLoading.set(false);
        console.error(err);
      }
    });
  }

  toggleEdit(): void {
    if (this.isEditing()) {
      const profile = this.userProfile();
      if (profile) {
        this.editFormName.set(profile.username);
        this.editFormEmail.set(profile.email);
        this.editFormAvatarUrl.set(profile.avatarUrl || '');
      }
    }
    this.isEditing.set(!this.isEditing());
    this.errorMessage.set('');
    this.successMessage.set('');
  }

  onSubmit(form: NgForm): void {
    if (form.invalid || this.isSubmitting()) {
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const updateData: UpdateProfile = {
      name: this.editFormName(),
      email: this.editFormEmail(),
      avatarUrl: this.editFormAvatarUrl()
    };

    this.userService.updateUser(updateData).subscribe({
      next: (res) => {
        this.userProfile.set(res);
        this.successMessage.set('Profile updated successfully!');
        this.isSubmitting.set(false);
        this.isEditing.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.message || err?.error?.msg || 'Failed to update profile. Please try again.');
        this.isSubmitting.set(false);
      }
    });
  }
}
