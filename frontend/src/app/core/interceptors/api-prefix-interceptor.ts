import { Injectable } from '@angular/core';
import {
    HttpInterceptor,
    HttpRequest,
    HttpHandler,
    HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class ApiPrefixInterceptor implements HttpInterceptor {

    private readonly GATEWAY_BASE_URL = 'http://localhost:10000';

    intercept(
        req: HttpRequest<any>,
        next: HttpHandler
    ): Observable<HttpEvent<any>> {
                
        if (req.url.startsWith('/api/')) {
            const apiReq = req.clone({
                url: `${this.GATEWAY_BASE_URL}${req.url}`
            });
            return next.handle(apiReq);
        }

        return next.handle(req);
    }
}
