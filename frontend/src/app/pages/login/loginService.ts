import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginPayload {
    identification: string;
    password: string;
}

export interface LoginResponse {
    token: string;
    role: string;
    message: string;
}

export interface RegisterPayload {
    email: string;
    name: string;
    password: string;
    role: string;
}

export interface RegisterResponse {
    msg: string;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
    private readonly loginPath = '/api/users/login';
    private readonly registerPath = '/api/users/register';

    constructor(private http: HttpClient) { }

    loginUser(userData: LoginPayload): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(this.loginPath, userData);
    }

    registerUser(userData: RegisterPayload): Observable<RegisterResponse> {
        return this.http.post<RegisterResponse>(this.registerPath, userData);
    }

    logeUser(userData: LoginPayload): Observable<LoginResponse> {
        return this.loginUser(userData);
    }
}
