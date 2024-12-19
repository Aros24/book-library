import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth-service.service';

@Injectable({
  providedIn: 'root',
})
export class TokenInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('Intercepted request:', req);

    const authToken = this.authService.getToken();

    if (authToken && authToken.trim() !== '') {
      req = req.clone({
        headers: req.headers.set('Authorization', `${authToken}`),
      });
      console.log('Token added to the request');
    } else {
      console.warn('No valid token found for the request.');
    }

    return next.handle(req).pipe(
      tap(event => {
        if (event instanceof HttpResponse) {
          const newToken = event.headers.get('Authorization');
          if (newToken) {
            console.log('New token received');
            this.authService.saveToken(newToken);
          } else {
            console.warn('No Authorization header found in the response.');
          }
        }
      })
    );
  }
}
