import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuard } from './auth-guard.guard';
import { BooksComponent } from './books/books.component';
import { RentComponent } from './rent/rent.component';
import { UsersComponent } from './users/users.component';
import { SettingsComponent } from './settings/settings.component';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent,
    children:[
      { path: 'books', component: BooksComponent },
      { path: 'rents', component: RentComponent},
      { path: 'users', component: UsersComponent},
      { path: 'settings', component: SettingsComponent}
  ] 
},
];
