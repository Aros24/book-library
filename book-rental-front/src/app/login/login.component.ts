import { Component, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  imports: [
    CommonModule,
    FormsModule,
    MatSnackBarModule,
  ],
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  firstName: string = '';
  lastName: string = '';
  isLoginMode: boolean = true;

  constructor(
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private apiService: ApiService
  ) {}

  toggleMode() {
    this.isLoginMode = !this.isLoginMode;
    alert(this.isLoginMode);
  }

  onSubmit(form: any) {
    if (!form.valid) {
      this.showSnackBar('Please fill out all required fields!', 'error-snackbar');
      return;
    }
  
    const payload = {
      email: this.email,
      password: this.password,
    };
    if(this.isLoginMode){
      this.apiService.post<{ token: string; role: string; user_public_id: string }>('/auth/v1/login', payload, true).subscribe(
        (response: HttpResponse<any>) => {
          const headers = response.headers;
      
          const authorizationToken = headers.get('Authorization');
      
          const body = response.body;
          const { role, user_public_id } = body;
      
          if (authorizationToken && role && user_public_id) {
            this.authService.saveToken(authorizationToken);
            this.authService.saveRole(role);
            this.authService.savePublicUser(user_public_id);
      
            this.showSnackBar('Login successful!', 'success-snackbar');
            this.router.navigate(['/dashboard']);
          } else {
            this.showSnackBar('Incomplete login response received!', 'warning-snackbar');
          }
        },
        (error) => {
          this.handleLoginError(error);
        }
      );
    }else{

      const registerPayload = {
        first_name: this.firstName,
        last_name: this.lastName,
        email: this.email,
        password: this.password,
      };
    
      this.apiService.post<{ token: string; role: string; user_public_id: string }>('/auth/v1/register', registerPayload, true).subscribe(
        (response: HttpResponse<any>) => {
          const body = response.body;
          const { role, user_public_id } = body;
      
          if (role && user_public_id) {
            this.showSnackBar('Register successful!', 'success-snackbar');
            this.toggleMode();
          } else {
            this.showSnackBar('Incomplete register response received!', 'warning-snackbar');
          }
        },
        (error) => {
          this.handleLoginError(error);
        }
      );
    }

  }

  private handleLoginError(error: any): void {
    if (error.status === 400) {
      this.showSnackBar('Invalid password. Please try again.', 'error-snackbar');
    } else if (error.status === 404) {
      this.showSnackBar('User not found. Please check your email.', 'error-snackbar');
    } else {
      this.showSnackBar('Login failed. Please check your credentials.', 'error-snackbar');
    }
  }

  private showSnackBar(message: string, panelClass: string) {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: [panelClass],
    });
  }
}
