package com.parking.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.api.model.Car;
import com.parking.api.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveCar() throws Exception {
        // Arrange
        Car newCar = new Car();
        newCar.setModelo("Honda Civic");
        newCar.setCor("Preto");
        newCar.setPlaca("ABC-1234");
        newCar.setNomeProprietario("João Silva");

        // Act - Create car
        String response = mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.modelo").value("Honda Civic"))
                .andExpect(jsonPath("$.placa").value("ABC-1234"))
                .andExpect(jsonPath("$.dataEntrada").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Car createdCar = objectMapper.readValue(response, Car.class);

        // Assert - Retrieve by ID
        mockMvc.perform(get("/api/cars/" + createdCar.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCar.getId()))
                .andExpect(jsonPath("$.modelo").value("Honda Civic"))
                .andExpect(jsonPath("$.placa").value("ABC-1234"));
    }

    @Test
    void shouldListAllCars() throws Exception {
        // Arrange - Create multiple cars
        Car car1 = new Car();
        car1.setModelo("Honda Civic");
        car1.setCor("Preto");
        car1.setPlaca("ABC-1234");
        car1.setNomeProprietario("João Silva");
        car1.setDataEntrada(LocalDateTime.now());
        carRepository.save(car1);

        Car car2 = new Car();
        car2.setModelo("Toyota Corolla");
        car2.setCor("Branco");
        car2.setPlaca("XYZ-5678");
        car2.setNomeProprietario("Maria Santos");
        car2.setDataEntrada(LocalDateTime.now());
        carRepository.save(car2);

        // Act & Assert
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].placa", containsInAnyOrder("ABC-1234", "XYZ-5678")));
    }

    @Test
    void shouldFindCarByPlaca() throws Exception {
        // Arrange
        Car car = new Car();
        car.setModelo("Honda Civic");
        car.setCor("Preto");
        car.setPlaca("ABC-1234");
        car.setNomeProprietario("João Silva");
        car.setDataEntrada(LocalDateTime.now());
        carRepository.save(car);

        // Act & Assert
        mockMvc.perform(get("/api/cars/placa/ABC-1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC-1234"))
                .andExpect(jsonPath("$.modelo").value("Honda Civic"));
    }

    @Test
    void shouldReturn404WhenCarNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/cars/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Carro não encontrado com ID: 999"));
    }

    @Test
    void shouldReturn409WhenPlacaAlreadyExists() throws Exception {
        // Arrange - Create first car
        Car car1 = new Car();
        car1.setModelo("Honda Civic");
        car1.setCor("Preto");
        car1.setPlaca("ABC-1234");
        car1.setNomeProprietario("João Silva");
        car1.setDataEntrada(LocalDateTime.now());
        carRepository.save(car1);

        // Act - Try to create car with same placa
        Car car2 = new Car();
        car2.setModelo("Toyota Corolla");
        car2.setCor("Branco");
        car2.setPlaca("ABC-1234"); // mesma placa
        car2.setNomeProprietario("Maria Santos");

        // Assert
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Já existe um carro registrado com a placa: ABC-1234"));
    }

    @Test
    void shouldUpdateCar() throws Exception {
        // Arrange - Create car
        Car car = new Car();
        car.setModelo("Honda Civic");
        car.setCor("Preto");
        car.setPlaca("ABC-1234");
        car.setNomeProprietario("João Silva");
        car.setDataEntrada(LocalDateTime.now());
        Car savedCar = carRepository.save(car);

        // Act - Update car
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic EX");
        updatedDetails.setCor("Azul");
        updatedDetails.setPlaca("ABC-1234");
        updatedDetails.setNomeProprietario("João Silva");

        // Assert
        mockMvc.perform(put("/api/cars/" + savedCar.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCar.getId()))
                .andExpect(jsonPath("$.modelo").value("Honda Civic EX"))
                .andExpect(jsonPath("$.cor").value("Azul"));
    }

    @Test
    void shouldRegisterCarExit() throws Exception {
        // Arrange - Create car
        Car car = new Car();
        car.setModelo("Honda Civic");
        car.setCor("Preto");
        car.setPlaca("ABC-1234");
        car.setNomeProprietario("João Silva");
        car.setDataEntrada(LocalDateTime.now());
        Car savedCar = carRepository.save(car);

        // Act - Register exit
        mockMvc.perform(patch("/api/cars/" + savedCar.getId() + "/exit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCar.getId()))
                .andExpect(jsonPath("$.dataSaida").isNotEmpty());
    }

    @Test
    void shouldDeleteCar() throws Exception {
        // Arrange - Create car
        Car car = new Car();
        car.setModelo("Honda Civic");
        car.setCor("Preto");
        car.setPlaca("ABC-1234");
        car.setNomeProprietario("João Silva");
        car.setDataEntrada(LocalDateTime.now());
        Car savedCar = carRepository.save(car);

        // Act - Delete car
        mockMvc.perform(delete("/api/cars/" + savedCar.getId()))
                .andExpect(status().isNoContent());

        // Assert - Car should not exist anymore
        mockMvc.perform(get("/api/cars/" + savedCar.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenCarDataIsInvalid() throws Exception {
        // Arrange - Car without required fields
        Car invalidCar = new Car();
        invalidCar.setCor("Preto");
        // Missing modelo and placa (required fields)

        // Act & Assert
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCar)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPreventUpdatingToExistingPlaca() throws Exception {
        // Arrange - Create two cars
        Car car1 = new Car();
        car1.setModelo("Honda Civic");
        car1.setCor("Preto");
        car1.setPlaca("ABC-1234");
        car1.setNomeProprietario("João Silva");
        car1.setDataEntrada(LocalDateTime.now());
        Car savedCar1 = carRepository.save(car1);

        Car car2 = new Car();
        car2.setModelo("Toyota Corolla");
        car2.setCor("Branco");
        car2.setPlaca("XYZ-5678");
        car2.setNomeProprietario("Maria Santos");
        car2.setDataEntrada(LocalDateTime.now());
        carRepository.save(car2);

        // Act - Try to update car1's placa to car2's placa
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic");
        updatedDetails.setCor("Preto");
        updatedDetails.setPlaca("XYZ-5678"); // placa já usada por car2
        updatedDetails.setNomeProprietario("João Silva");

        // Assert
        mockMvc.perform(put("/api/cars/" + savedCar1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Já existe um carro registrado com a placa: XYZ-5678"));
    }

    @Test
    void shouldAllowUpdatingCarWithSamePlaca() throws Exception {
        // Arrange - Create car
        Car car = new Car();
        car.setModelo("Honda Civic");
        car.setCor("Preto");
        car.setPlaca("ABC-1234");
        car.setNomeProprietario("João Silva");
        car.setDataEntrada(LocalDateTime.now());
        Car savedCar = carRepository.save(car);

        // Act - Update car keeping the same placa
        Car updatedDetails = new Car();
        updatedDetails.setModelo("Honda Civic EX");
        updatedDetails.setCor("Vermelho");
        updatedDetails.setPlaca("ABC-1234"); // mesma placa
        updatedDetails.setNomeProprietario("João Silva");

        // Assert
        mockMvc.perform(put("/api/cars/" + savedCar.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Honda Civic EX"))
                .andExpect(jsonPath("$.cor").value("Vermelho"))
                .andExpect(jsonPath("$.placa").value("ABC-1234"));
    }
}
