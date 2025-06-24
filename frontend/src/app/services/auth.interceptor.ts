import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { catchError, EMPTY, Observable, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private authService: AuthService, private router: Router) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (request.url.includes('/api/login') ||
            (request.url.includes('/api/user') && request.method === 'POST')) {
            console.log('Skipping token for auth endpoint:', request.url);
            return next.handle(request);
        }

        console.log('AuthInterceptor triggered for request to:', request.url);

        // Get the JWT Token
        const token = this.authService.getToken();
        console.log('Token found:', token ? 'Yes (length: ' + token.length + ')' : 'No');

        // If there is a JWT token, authorize the HTTP request
        if (token) {
            // Clone the request and add the JWT Token
            const authRequest = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${token}`
                }
            });

            console.log(authRequest);
            console.log("TOKEN: " + token);

            return next.handle(authRequest).pipe(
                catchError(error => {
                    console.error('Request error:', error.status, error.message);
                    if (error.status === 401 || error.status === 403) {
                        console.log('Unauthorized, redirecting to login');
                        this.router.navigate(['/sign-in']);
                    }

                    return throwError(() => error);
                })
            );
        } else {
            console.log('Unauthorized, redirecting to login');
            this.authService.logout();
            this.router.navigate(['/sign-in']);
            return EMPTY;
        }
    }
}
