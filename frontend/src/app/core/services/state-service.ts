import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject} from "rxjs";
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

    getMyInfo(): void {
        this.http.get<Me>("/api/users/me")
            .subscribe({
                next: (user) => {
                    this.setUser(user);
                },
                error: (err) => console.error('Failed to load user', err)
            });
    }

    setUser(user: Me | null): void {
        this.currentUserSubject.next(user);
        if (user) {
            localStorage.setItem('user', JSON.stringify(user));
        } else {
            localStorage.removeItem('user');
        }
    }

    clearUser() {
        this.setUser(null);
    }
}