import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth-service.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [FormsModule],
})
export class RegisterComponent {
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  password: string = '';

  constructor(private http: HttpClient, private authService: AuthService) {}

  onRegister() {
    const registerPayload = {
      first_name: this.firstName,
      last_name: this.lastName,
      email: this.email,
      password: this.password,
    };

    this.http
      .post('http://127.0.0.1:8080/auth/v1/register', registerPayload, { observe: 'response' })
      .subscribe(
        (response) => {
          const token = response.headers.get('Authorization');
          if (token) {
            this.authService.saveToken(token); 
            alert('Registration successful!' + token);
          } else {
            alert('Registration successful, but no token received.');
          }
        },
        (error) => {
          alert('Registration failed. Please try again.');
        }
      );
  }
}
