import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { DashboardComponent } from './components/dashboard/dashboard';
import { EmployeeManagementComponent } from './components/employee-management/employee-management';
import { ParkingManagementComponent } from './components/parking-management/parking-management';
import { authGuard, adminGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'parking', pathMatch: 'full' },
      { path: 'parking', component: ParkingManagementComponent },
      {
        path: 'employees',
        component: EmployeeManagementComponent,
        canActivate: [adminGuard]
      }
    ]
  },
  { path: '**', redirectTo: '/login' }
];
