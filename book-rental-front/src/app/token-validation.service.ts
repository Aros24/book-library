import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class TokenValidationService {
  private isTokenValid: boolean = true;
  private validationInterval: any;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  startValidation(): void {
    setTimeout(() => {
      this.validateToken();
      this.validationInterval = setInterval(() => this.validateToken(), 600000);
    }, 0);
  }

  stopValidation(): void {
    clearInterval(this.validationInterval);
  }

  async validateTokenOnDemand(): Promise<boolean> {
    try {
      await this.apiService
        .get('/v1/books', { page: 0, size: 1 })
        .toPromise();
      return true;
    } catch (error: any) {
      if (error?.status === 403) {
        this.handleInvalidToken();
      }
      return false;
    }
  }
  
  private validateToken(): void {
    this.apiService.get('/v1/books', { page: 0, size: 1 }).subscribe(
      () => {
        alert(3);
        this.isTokenValid = true;
      },
      (error) => {
        if (error.status === 403) {
          alert(4);
          this.isTokenValid = false;
          this.handleInvalidToken();
        }
      }
    );
  }

  private handleInvalidToken(): void {
    this.stopValidation();
    localStorage.clear();
    this.snackBar.open('Session expired. Please log in again.', 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
    });
    this.router.navigate(['/login']);
  }
}