import { Component, ViewEncapsulation } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [RouterOutlet,RouterModule],
  encapsulation: ViewEncapsulation.None,
  standalone: true,
})
export class DashboardComponent {
  constructor(private router: Router, private snackBar: MatSnackBar) {}

  ngOnInit(): void {}

  logout(): void {
    localStorage.clear();

    this.snackBar.open('Successfully logged out', 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['success-snackbar'],
    });

    this.router.navigate(['/login']);
  }
}
