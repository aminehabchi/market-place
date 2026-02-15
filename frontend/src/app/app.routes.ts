import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { SellerDashboard } from './pages/seller-dashboard/seller-dashboard';
import { NotFound } from './pages/not-found/not-found';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Profile } from './pages/profile/profile';

export const routes: Routes = [
    { path: '', component: Home },
    { path: 'dashboard', component: SellerDashboard },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'profile', component: Profile },
    { path: 'not-found', component: NotFound },
    { path: '**', redirectTo: 'not-found' }
];
