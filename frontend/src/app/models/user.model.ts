export interface User {
  id: number;
  username: string;
  role: string;
  createdAt?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  username: string;
  role: string;
  message: string;
}

export interface UserRequest {
  username: string;
  password: string;
  role: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}
