import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const guestOnlyGuard: CanActivateFn = () => {
	const router = inject(Router);
	const token = localStorage.getItem('token');

	if (token) {
		return router.createUrlTree(['/']);
	}

	return true;
};
