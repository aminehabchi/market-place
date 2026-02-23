import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  isOpen = false;
  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: object
  ) {}

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  isAuthenticated(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem('token');
    }

    return false;
  }

  logout(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.isOpen = false;
    this.router.navigateByUrl('/login');
  }
}
