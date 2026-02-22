import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  isOpen = false;
  token: String | null = '';
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  areWeLoggedIn(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      this.token = localStorage.getItem('token');
      if (this.token == null) {
        return true
      }
      return false;
    }
    return true;
  }
}
