import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { User } from '../models/User';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<User> {
    const credentials = btoa(`${email}:${password}`);
    const headers = new HttpHeaders({
      'Authorization': `Basic ${credentials}`
    });

    return this.http.post<ApiResponse<any>>(
      `${this.apiUrl}/login`,
      {},
      { headers }
    ).pipe(
      map(response => response.data)
    );
  }

   register(username: string, email: string, password: string, role: string = 'ROLE_GUEST'): Observable<User> {
    const body = { username, email, password, role };

    return this.http.post<ApiResponse<any>>(
      `${this.apiUrl}/register`,
      body
    ).pipe(
      map(response => response.data)
    );
  }

}
