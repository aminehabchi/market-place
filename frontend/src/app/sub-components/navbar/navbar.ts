import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { StateService } from '../../core/services/state-service';
import { Role } from '../../core/models/Role';
import { User } from '../../core/models/User';
import { Me } from '../../core/services/users-service';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  isOpen = false;
  isSeller = signal(false);
  constructor(
    private router: Router,
    private stateService: StateService,
    @Inject(PLATFORM_ID) private platformId: object
  ) { }

  ngOnInit() {
    this.stateService.currentUser$.subscribe((user: Me | null) => {

      if (user && user.role === "SELLER") {
        this.isSeller.set(true);
      } else {
        this.isSeller.set(false);
      }
    });

  }

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
    this.stateService.clearUser();
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.isOpen = false;
    this.router.navigateByUrl('/login');
  }
}
