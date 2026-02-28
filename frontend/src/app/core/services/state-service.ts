import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject} from "rxjs";
import { ApiResponse } from "../models/ApiResponse";
import { Me } from "./users-service";

@Injectable({
    providedIn: 'root',
})
export class StateService {

    public currentUserSubject = new BehaviorSubject<Me | null>(null);

    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            this.currentUserSubject.next(JSON.parse(storedUser));
        }
    }

    getMyInfo() {
        this.http.get<ApiResponse<Me>>("/api/users/me")
            .subscribe({
                next: (res: any) => {
                    this.currentUserSubject.next(res);
                },
                error: (err) => console.error('Failed to load user', err)
            });
    }
    clearUser() {
        this.currentUserSubject.next(null);
        localStorage.removeItem('user');
    }
}