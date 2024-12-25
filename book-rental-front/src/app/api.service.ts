import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ConfigService } from './config-service.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private http: HttpClient, private configService: ConfigService) {}

  private get baseUrl(): string {
    return this.configService.apiBaseUrl
  }

  private getAuthHeaders(): HttpHeaders {
    const authToken = localStorage.getItem('authToken') || '';
    return new HttpHeaders({
      Authorization: `${authToken}`,
    });
  }

  // Generic GET method
  get<T>(endpoint: string, params: any = {}): Observable<T> {
    const headers = this.getAuthHeaders();
    return this.http
      .get<T>(`${this.baseUrl}${endpoint}`, { headers, params })
      .pipe(catchError(this.handleError));
  }

  // Generic POST method
  post<T>(endpoint: string, body: any = {}, observeResponse: boolean = false): Observable<any> {
    const headers = this.getAuthHeaders();
    const options: any = {
      headers,
      body,
    };

    if (observeResponse) {
      options.observe = 'response';
    }

    return this.http
      .post<T>(`${this.baseUrl}${endpoint}`, body, options)
      .pipe(catchError(this.handleError));
  }


  // Generic PUT method
  put<T>(endpoint: string, body: any = {}): Observable<T> {
    const headers = this.getAuthHeaders();
    return this.http
      .put<T>(`${this.baseUrl}${endpoint}`, body, { headers })
      .pipe(catchError(this.handleError));
  }

  // Generic PATCH method
  patch<T>(endpoint: string, body: any = {}): Observable<T> {
    const headers = this.getAuthHeaders();
    return this.http
      .patch<T>(`${this.baseUrl}${endpoint}`, body, { headers })
      .pipe(catchError(this.handleError));
  }

  // Generic DELETE method
  delete<T>(endpoint: string): Observable<T> {
    const headers = this.getAuthHeaders();
    return this.http
      .delete<T>(`${this.baseUrl}${endpoint}`, { headers })
      .pipe(catchError(this.handleError));
  }

  // Handle errors globally
  private handleError(error: any): Observable<never> {
    console.error('API error:', error);
    return throwError(error);
  }
}
