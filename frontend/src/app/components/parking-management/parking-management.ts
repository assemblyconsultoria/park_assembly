import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarService } from '../../services/car';
import { Car } from '../../models/car.model';
import { FilterPipe } from '../../pipes/filter.pipe';

@Component({
  selector: 'app-parking-management',
  standalone: true,
  imports: [CommonModule, FormsModule, FilterPipe],
  templateUrl: './parking-management.html',
  styleUrl: './parking-management.scss'
})
export class ParkingManagementComponent implements OnInit {
  cars: Car[] = [];
  showModal = false;
  isEditMode = false;
  currentCar: Car = { modelo: '', cor: '', placa: '', nomeProprietario: '' };
  selectedCarId: number | null = null;
  searchPlaca = '';
  errorMessage = '';
  successMessage = '';
  filterStatus: 'all' | 'active' | 'exited' = 'all';

  constructor(private carService: CarService) {}

  ngOnInit(): void {
    this.loadCars();
  }

  loadCars(): void {
    this.carService.getAllCars().subscribe({
      next: (cars) => {
        this.cars = cars;
      },
      error: (error) => {
        this.showError('Erro ao carregar carros');
      }
    });
  }

  get filteredCars(): Car[] {
    let filtered = this.cars;
    
    if (this.filterStatus === 'active') {
      filtered = filtered.filter(car => !car.dataSaida);
    } else if (this.filterStatus === 'exited') {
      filtered = filtered.filter(car => car.dataSaida);
    }
    
    if (this.searchPlaca) {
      filtered = filtered.filter(car => 
        car.placa.toLowerCase().includes(this.searchPlaca.toLowerCase())
      );
    }
    
    return filtered;
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.currentCar = { modelo: '', cor: '', placa: '', nomeProprietario: '' };
    this.showModal = true;
    this.clearMessages();
  }

  openEditModal(car: Car): void {
    this.isEditMode = true;
    this.selectedCarId = car.id!;
    this.currentCar = {
      modelo: car.modelo,
      cor: car.cor,
      placa: car.placa,
      nomeProprietario: car.nomeProprietario
    };
    this.showModal = true;
    this.clearMessages();
  }

  closeModal(): void {
    this.showModal = false;
    this.clearMessages();
  }

  saveCar(): void {
    if (this.isEditMode && this.selectedCarId) {
      this.carService.updateCar(this.selectedCarId, this.currentCar).subscribe({
        next: () => {
          this.showSuccess('Carro atualizado com sucesso');
          this.loadCars();
          this.closeModal();
        },
        error: (error) => {
          this.showError(error.error?.message || 'Erro ao atualizar carro');
        }
      });
    } else {
      this.carService.createCar(this.currentCar).subscribe({
        next: () => {
          this.showSuccess('Entrada registrada com sucesso');
          this.loadCars();
          this.closeModal();
        },
        error: (error) => {
          this.showError(error.error?.message || 'Erro ao registrar entrada');
        }
      });
    }
  }

  registerExit(id: number): void {
    if (confirm('Registrar saida deste veiculo?')) {
      this.carService.registerExit(id).subscribe({
        next: () => {
          this.showSuccess('Saida registrada com sucesso');
          this.loadCars();
        },
        error: (error) => {
          this.showError('Erro ao registrar saida');
        }
      });
    }
  }

  deleteCar(id: number): void {
    if (confirm('Tem certeza que deseja excluir este registro?')) {
      this.carService.deleteCar(id).subscribe({
        next: () => {
          this.showSuccess('Registro excluido com sucesso');
          this.loadCars();
        },
        error: (error) => {
          this.showError('Erro ao excluir registro');
        }
      });
    }
  }

  searchByPlaca(): void {
    if (this.searchPlaca.trim()) {
      this.carService.getCarByPlaca(this.searchPlaca).subscribe({
        next: (car) => {
          this.cars = [car];
        },
        error: (error) => {
          this.showError('Placa nao encontrada');
          this.loadCars();
        }
      });
    } else {
      this.loadCars();
    }
  }

  clearSearch(): void {
    this.searchPlaca = '';
    this.loadCars();
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

  formatDateTime(dateTime: string | undefined): string {
    if (!dateTime) return '-';
    return new Date(dateTime).toLocaleString('pt-BR');
  }

  isActive(car: Car): boolean {
    return !car.dataSaida;
  }
}
