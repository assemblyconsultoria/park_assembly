package com.parking.api.service;

import com.parking.api.exception.DuplicatePlacaException;
import com.parking.api.exception.ResourceNotFoundException;
import com.parking.api.model.Car;
import com.parking.api.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    private Car testCar;

    @BeforeEach
    void setUp() {
        testCar = new Car();
        testCar.setId(1L);
        testCar.setModelo("Honda Civic");
        testCar.setCor("Preto");
        testCar.setPlaca("ABC-1234");
        testCar.setNomeProprietario("João Silva");
        testCar.setDataEntrada(LocalDateTime.now());
    }

    @Test
    void getAllCars_ShouldReturnListOfCars() {
        // Arrange
        Car car2 = new Car();
        car2.setId(2L);
        car2.setModelo("Toyota Corolla");
        car2.setCor("Branco");
        car2.setPlaca("XYZ-5678");
        car2.setNomeProprietario("Maria Santos");

        List<Car> cars = Arrays.asList(testCar, car2);
        when(carRepository.findAll()).thenReturn(cars);

        // Act
        List<Car> result = carService.getAllCars();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testCar, car2);
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void getAllCars_ShouldReturnEmptyList_WhenNoCarsExist() {
        // Arrange
        when(carRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Car> result = carService.getAllCars();

        // Assert
        assertThat(result).isEmpty();
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void getCarById_ShouldReturnCar_WhenCarExists() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act
        Car result = carService.getCarById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPlaca()).isEqualTo("ABC-1234");
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void getCarById_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> carService.getCarById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Carro não encontrado com ID: 999");
        verify(carRepository, times(1)).findById(999L);
    }

    @Test
    void getCarByPlaca_ShouldReturnCar_WhenCarExists() {
        // Arrange
        when(carRepository.findByPlaca("ABC-1234")).thenReturn(Optional.of(testCar));

        // Act
        Car result = carService.getCarByPlaca("ABC-1234");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPlaca()).isEqualTo("ABC-1234");
        verify(carRepository, times(1)).findByPlaca("ABC-1234");
    }

    @Test
    void getCarByPlaca_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findByPlaca(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> carService.getCarByPlaca("XYZ-9999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Carro não encontrado com placa: XYZ-9999");
        verify(carRepository, times(1)).findByPlaca("XYZ-9999");
    }

    @Test
    void createCar_ShouldSaveCar_WhenPlacaDoesNotExist() {
        // Arrange
        when(carRepository.existsByPlaca("ABC-1234")).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        Car result = carService.createCar(testCar);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPlaca()).isEqualTo("ABC-1234");
        verify(carRepository, times(1)).existsByPlaca("ABC-1234");
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void createCar_ShouldThrowException_WhenPlacaAlreadyExists() {
        // Arrange
        when(carRepository.existsByPlaca("ABC-1234")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> carService.createCar(testCar))
                .isInstanceOf(DuplicatePlacaException.class)
                .hasMessageContaining("Já existe um carro registrado com a placa: ABC-1234");
        verify(carRepository, times(1)).existsByPlaca("ABC-1234");
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCar_ShouldUpdateCar_WhenPlacaNotChanged() {
        // Arrange
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic EX");
        updatedDetails.setCor("Azul");
        updatedDetails.setPlaca("ABC-1234"); // mesma placa
        updatedDetails.setNomeProprietario("João Silva");

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        Car result = carService.updateCar(1L, updatedDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getModelo()).isEqualTo("Honda Civic EX");
        assertThat(result.getCor()).isEqualTo("Azul");
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void updateCar_ShouldUpdateCar_WhenPlacaChangedAndNotDuplicate() {
        // Arrange
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic");
        updatedDetails.setCor("Preto");
        updatedDetails.setPlaca("DEF-5678"); // nova placa
        updatedDetails.setNomeProprietario("João Silva");

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.existsByPlaca("DEF-5678")).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        Car result = carService.updateCar(1L, updatedDetails);

        // Assert
        assertThat(result).isNotNull();
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).existsByPlaca("DEF-5678");
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void updateCar_ShouldThrowException_WhenNewPlacaAlreadyExists() {
        // Arrange
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic");
        updatedDetails.setCor("Preto");
        updatedDetails.setPlaca("XYZ-9999"); // nova placa que já existe
        updatedDetails.setNomeProprietario("João Silva");

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.existsByPlaca("XYZ-9999")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> carService.updateCar(1L, updatedDetails))
                .isInstanceOf(DuplicatePlacaException.class)
                .hasMessageContaining("Já existe um carro registrado com a placa: XYZ-9999");
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).existsByPlaca("XYZ-9999");
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCar_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        Car updatedDetails = new Car();
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> carService.updateCar(999L, updatedDetails))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Carro não encontrado com ID: 999");
        verify(carRepository, times(1)).findById(999L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void registerExit_ShouldSetExitDate() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        Car result = carService.registerExit(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDataSaida()).isNotNull();
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void registerExit_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> carService.registerExit(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Carro não encontrado com ID: 999");
        verify(carRepository, times(1)).findById(999L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void deleteCar_ShouldDeleteCar_WhenCarExists() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        doNothing().when(carRepository).delete(testCar);

        // Act
        carService.deleteCar(1L);

        // Assert
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).delete(testCar);
    }

    @Test
    void deleteCar_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> carService.deleteCar(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Carro não encontrado com ID: 999");
        verify(carRepository, times(1)).findById(999L);
        verify(carRepository, never()).delete(any(Car.class));
    }
}
