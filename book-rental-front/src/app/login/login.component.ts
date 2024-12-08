import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth-service.service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [FormsModule, HttpClientModule],
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  constructor(
    private http: HttpClient, 
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin() {
    const loginPayload = { email: this.email, password: this.password };

    this.http.post('http://127.0.0.1:8080/auth/v1/login', loginPayload, { observe: 'response', withCredentials: true})
    .subscribe(
      (response: any) => {
        const token = response.headers.get('Authorization'); // Get token from headers\
        alert(token);
        this.router.navigate(['/dashboard']);
        if (token) {
          this.authService.saveToken(token); // Save the token
          alert('Login successful!');
        } else {
          alert('Login successful, but no token received.');
        }
      },
      (error) => {
        alert('Login failed. Please check your credentials.');
      }
    );
  }
}
