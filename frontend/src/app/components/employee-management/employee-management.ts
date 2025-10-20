import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user';
import { AuthService } from '../../services/auth';
import { User, UserRequest, ChangePasswordRequest } from '../../models/user.model';

@Component({
  selector: 'app-employee-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee-management.html',
  styleUrl: './employee-management.scss'
})
export class EmployeeManagementComponent implements OnInit {
  users: User[] = [];
  showModal = false;
  showPasswordModal = false;
  isEditMode = false;
  currentUser: UserRequest = { username: '', password: '', role: 'USER' };
  selectedUserId: number | null = null;
  passwordChange: ChangePasswordRequest = { oldPassword: '', newPassword: '' };
  errorMessage = '';
  successMessage = '';

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        this.showError('Erro ao carregar usuarios');
      }
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.currentUser = { username: '', password: '', role: 'USER' };
    this.showModal = true;
    this.clearMessages();
  }

  openEditModal(user: User): void {
    this.isEditMode = true;
    this.selectedUserId = user.id;
    this.currentUser = {
      username: user.username,
      password: '',
      role: user.role
    };
    this.showModal = true;
    this.clearMessages();
  }

  openPasswordModal(userId: number): void {
    this.selectedUserId = userId;
    this.passwordChange = { oldPassword: '', newPassword: '' };
    this.showPasswordModal = true;
    this.clearMessages();
  }

  closeModal(): void {
    this.showModal = false;
    this.showPasswordModal = false;
    this.clearMessages();
  }

  saveUser(): void {
    if (this.isEditMode && this.selectedUserId) {
      this.userService.updateUser(this.selectedUserId, this.currentUser).subscribe({
        next: () => {
          this.showSuccess('Usuario atualizado com sucesso');
          this.loadUsers();
          this.closeModal();
        },
        error: (error) => {
          this.showError(error.error?.message || 'Erro ao atualizar usuario');
        }
      });
    } else {
      this.userService.createUser(this.currentUser).subscribe({
        next: () => {
          this.showSuccess('Usuario criado com sucesso');
          this.loadUsers();
          this.closeModal();
        },
        error: (error) => {
          this.showError(error.error?.message || 'Erro ao criar usuario');
        }
      });
    }
  }

  changePassword(): void {
    if (this.selectedUserId) {
      this.userService.changePassword(this.selectedUserId, this.passwordChange).subscribe({
        next: () => {
          this.showSuccess('Senha alterada com sucesso');
          this.closeModal();
        },
        error: (error) => {
          this.showError(error.error?.message || 'Erro ao alterar senha');
        }
      });
    }
  }

  deleteUser(id: number): void {
    if (confirm('Tem certeza que deseja excluir este usuario?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.showSuccess('Usuario excluido com sucesso');
          this.loadUsers();
        },
        error: (error) => {
          this.showError('Erro ao excluir usuario');
        }
      });
    }
  }

  isCurrentUser(userId: number): boolean {
    return this.authService.currentUserValue?.id === userId;
  }

  showError(message: string): void {
    this.errorMessage = message;
    setTimeout(() => this.errorMessage = '', 5000);
  }

  showSuccess(message: string): void {
    this.successMessage = message;
    setTimeout(() => this.successMessage = '', 5000);
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }
}
