import { isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { StateService } from '../services/state-service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
	const platformId = inject(PLATFORM_ID);
	const router = inject(Router);
	const stateService = inject(StateService);
	const isBrowser = isPlatformBrowser(platformId);
	const token = isBrowser ? localStorage.getItem('token') : null;

	if (!token) {
		return next(req);
	}

	const authReq = req.clone({
		setHeaders: {
			Authorization: `Bearer ${token}`
		}
	});

	return next(authReq).pipe(
		catchError((error: unknown) => {
			if (
				isBrowser &&
				error instanceof HttpErrorResponse &&
				error.status === 401
			) {
				stateService.clearUser();
				localStorage.removeItem('token');
				localStorage.removeItem('role');
				if (router.url !== '/login') {
					void router.navigateByUrl('/login');
				}
			}

			return throwError(() => error);
		})
	);
};
