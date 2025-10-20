package com.parking.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.api.exception.DuplicatePlacaException;
import com.parking.api.exception.ResourceNotFoundException;
import com.parking.api.model.Car;
import com.parking.api.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
        testCar.setDataEntrada(LocalDateTime.of(2025, 10, 20, 10, 30));
    }

    @Test
    void getAllCars_ShouldReturnListOfCars() throws Exception {
        // Arrange
        Car car2 = new Car();
        car2.setId(2L);
        car2.setModelo("Toyota Corolla");
        car2.setCor("Branco");
        car2.setPlaca("XYZ-5678");
        car2.setNomeProprietario("Maria Santos");
        car2.setDataEntrada(LocalDateTime.of(2025, 10, 20, 11, 0));

        List<Car> cars = Arrays.asList(testCar, car2);
        when(carService.getAllCars()).thenReturn(cars);

        // Act & Assert
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].placa").value("ABC-1234"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].placa").value("XYZ-5678"));

        verify(carService, times(1)).getAllCars();
    }

    @Test
    void getAllCars_ShouldReturnEmptyList_WhenNoCarsExist() throws Exception {
        // Arrange
        when(carService.getAllCars()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carService, times(1)).getAllCars();
    }

    @Test
    void getCarById_ShouldReturnCar_WhenCarExists() throws Exception {
        // Arrange
        when(carService.getCarById(1L)).thenReturn(testCar);

        // Act & Assert
        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.modelo").value("Honda Civic"))
                .andExpect(jsonPath("$.placa").value("ABC-1234"))
                .andExpect(jsonPath("$.nomeProprietario").value("João Silva"));

        verify(carService, times(1)).getCarById(1L);
    }

    @Test
    void getCarById_ShouldReturn404_WhenCarNotFound() throws Exception {
        // Arrange
        when(carService.getCarById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Carro não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/cars/999"))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).getCarById(999L);
    }

    @Test
    void getCarByPlaca_ShouldReturnCar_WhenCarExists() throws Exception {
        // Arrange
        when(carService.getCarByPlaca("ABC-1234")).thenReturn(testCar);

        // Act & Assert
        mockMvc.perform(get("/api/cars/placa/ABC-1234"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.placa").value("ABC-1234"))
                .andExpect(jsonPath("$.modelo").value("Honda Civic"));

        verify(carService, times(1)).getCarByPlaca("ABC-1234");
    }

    @Test
    void getCarByPlaca_ShouldReturn404_WhenCarNotFound() throws Exception {
        // Arrange
        when(carService.getCarByPlaca(anyString()))
                .thenThrow(new ResourceNotFoundException("Carro não encontrado com placa: XYZ-9999"));

        // Act & Assert
        mockMvc.perform(get("/api/cars/placa/XYZ-9999"))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).getCarByPlaca("XYZ-9999");
    }

    @Test
    void createCar_ShouldReturnCreated_WhenCarIsValid() throws Exception {
        // Arrange
        Car newCar = new Car();
        newCar.setModelo("Honda Civic");
        newCar.setCor("Preto");
        newCar.setPlaca("ABC-1234");
        newCar.setNomeProprietario("João Silva");

        when(carService.createCar(any(Car.class))).thenReturn(testCar);

        // Act & Assert
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.placa").value("ABC-1234"));

        verify(carService, times(1)).createCar(any(Car.class));
    }

    @Test
    void createCar_ShouldReturn400_WhenCarIsInvalid() throws Exception {
        // Arrange - Car sem modelo (campo obrigatório)
        Car invalidCar = new Car();
        invalidCar.setCor("Preto");
        invalidCar.setPlaca("ABC-1234");

        // Act & Assert
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCar)))
                .andExpect(status().isBadRequest());

        verify(carService, never()).createCar(any(Car.class));
    }

    @Test
    void createCar_ShouldReturn409_WhenPlacaAlreadyExists() throws Exception {
        // Arrange
        Car newCar = new Car();
        newCar.setModelo("Honda Civic");
        newCar.setCor("Preto");
        newCar.setPlaca("ABC-1234");
        newCar.setNomeProprietario("João Silva");

        when(carService.createCar(any(Car.class)))
                .thenThrow(new DuplicatePlacaException("Já existe um carro registrado com a placa: ABC-1234"));

        // Act & Assert
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isConflict());

        verify(carService, times(1)).createCar(any(Car.class));
    }

    @Test
    void updateCar_ShouldReturnUpdatedCar_WhenCarExists() throws Exception {
        // Arrange
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic EX");
        updatedDetails.setCor("Azul");
        updatedDetails.setPlaca("ABC-1234");
        updatedDetails.setNomeProprietario("João Silva");

        Car updatedCar = new Car();
        updatedCar.setId(1L);
        updatedCar.setModelo("Honda Civic EX");
        updatedCar.setCor("Azul");
        updatedCar.setPlaca("ABC-1234");
        updatedCar.setNomeProprietario("João Silva");

        when(carService.updateCar(eq(1L), any(Car.class))).thenReturn(updatedCar);

        // Act & Assert
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.modelo").value("Honda Civic EX"))
                .andExpect(jsonPath("$.cor").value("Azul"));

        verify(carService, times(1)).updateCar(eq(1L), any(Car.class));
    }

    @Test
    void updateCar_ShouldReturn404_WhenCarNotFound() throws Exception {
        // Arrange
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic");
        updatedDetails.setCor("Preto");
        updatedDetails.setPlaca("ABC-1234");
        updatedDetails.setNomeProprietario("João Silva");

        when(carService.updateCar(anyLong(), any(Car.class)))
                .thenThrow(new ResourceNotFoundException("Carro não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/cars/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).updateCar(eq(999L), any(Car.class));
    }

    @Test
    void updateCar_ShouldReturn409_WhenNewPlacaAlreadyExists() throws Exception {
        // Arrange
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic");
        updatedDetails.setCor("Preto");
        updatedDetails.setPlaca("XYZ-9999");
        updatedDetails.setNomeProprietario("João Silva");

        when(carService.updateCar(anyLong(), any(Car.class)))
                .thenThrow(new DuplicatePlacaException("Já existe um carro registrado com a placa: XYZ-9999"));

        // Act & Assert
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isConflict());

        verify(carService, times(1)).updateCar(eq(1L), any(Car.class));
    }

    @Test
    void registerExit_ShouldReturnCarWithExitDate() throws Exception {
        // Arrange
        testCar.setDataSaida(LocalDateTime.of(2025, 10, 20, 15, 45));
        when(carService.registerExit(1L)).thenReturn(testCar);

        // Act & Assert
        mockMvc.perform(patch("/api/cars/1/exit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.dataSaida").isNotEmpty());

        verify(carService, times(1)).registerExit(1L);
    }

    @Test
    void registerExit_ShouldReturn404_WhenCarNotFound() throws Exception {
        // Arrange
        when(carService.registerExit(anyLong()))
                .thenThrow(new ResourceNotFoundException("Carro não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(patch("/api/cars/999/exit"))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).registerExit(999L);
    }

    @Test
    void deleteCar_ShouldReturnNoContent_WhenCarExists() throws Exception {
        // Arrange
        doNothing().when(carService).deleteCar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());

        verify(carService, times(1)).deleteCar(1L);
    }

    @Test
    void deleteCar_ShouldReturn404_WhenCarNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Carro não encontrado com ID: 999"))
                .when(carService).deleteCar(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/cars/999"))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).deleteCar(999L);
    }
}
