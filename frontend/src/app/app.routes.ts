import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { SellerDashboard } from './pages/seller-dashboard/seller-dashboard';
import { NotFound } from './pages/not-found/not-found';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';

export const routes: Routes = [
    { path: '', component: Home },
    { path: 'seller-dashboard', component: SellerDashboard },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'not-found', component: NotFound },
    { path: '**', redirectTo: 'not-found' }
];
