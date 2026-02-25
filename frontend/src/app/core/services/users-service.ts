import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { env } from '../../../env/env';

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
  image: string;
}

export interface Me {
  email: string;
  name: string;
  password: string;
  role: string;
  avatar: File;
}

export interface UpdateProfile {
  name: string;
  password: string;
}

export interface Response {
  msg: string;
}

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  private readonly loginPath = '/api/users/login';
  private readonly registerPath = '/api/users/register';
  private readonly mediaPath = '/api/media/register';

  constructor(private http: HttpClient) { }

  loginUser(userData: LoginPayload): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(env.apiUrl + this.loginPath, userData);
  }

  meUser(): Observable<Me> {
    return this.http.get<Me>(`${env.apiUrl}/api/users/me`);
  }

  registerUser(userData: RegisterPayload): Observable<Response> {
    // const formData = new FormData();
    // const infoBlob = new Blob([JSON.stringify(userData)], {
    //   type: 'application/json'
    // });

    // formData.append('info', infoBlob);

    return this.http.post<Response>(env.apiUrl + this.registerPath, userData);
  }

  meImage() {

  }

  registerUserWithAvatar(avatar?: File | null): Observable<Response> {
    const formData = new FormData();
    // const infoBlob = new Blob([JSON.stringify(userData)], {
    //   type: 'application/json'
    // });

    // formData.append('info', infoBlob);
    if (avatar) {
      formData.append('avatar', avatar, avatar.name);
    }

    return this.http.post<Response>(env.apiUrl + this.mediaPath, formData);
  }

  updateUser(userData: UpdateProfile): Observable<Response> {
    // const formData = new FormData();
    // const infoBlob = new Blob([JSON.stringify(userData)], {
    //   type: 'application/json'
    // });

    // formData.append('info', infoBlob);
    // if (avatar) {
    //   formData.append('avatar', avatar, avatar.name);
    // }

    return this.http.put<Response>(env.apiUrl + this.registerPath, userData);
  }

  updateAvatar(oldAvatar: File, newAvatar: File): Observable<Response> {
    const formData = new FormData();
    if (oldAvatar) {
      formData.append('oldAvatar', oldAvatar, oldAvatar.name);
    }
    if (newAvatar) {
      formData.append('newAvatar', newAvatar, newAvatar.name);
    }
    return this.http.put<Response>(env.apiUrl + this.mediaPath, formData);
  }

  logeUser(userData: LoginPayload): Observable<LoginResponse> {
    return this.loginUser(userData);
  }
}
