package com.parking.api.exception;

import com.parking.api.controller.CarController;
import com.parking.api.model.Car;
import com.parking.api.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    void shouldHandleResourceNotFoundException() throws Exception {
        // Arrange
        when(carService.getCarById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Carro não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/cars/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Carro não encontrado com ID: 999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldHandleDuplicatePlacaException() throws Exception {
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
                        .content("{\"modelo\":\"Honda Civic\",\"cor\":\"Preto\",\"placa\":\"ABC-1234\",\"nomeProprietario\":\"João Silva\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Já existe um carro registrado com a placa: ABC-1234"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldHandleValidationException_MissingModelo() throws Exception {
        // Act & Assert - Car without modelo (required field)
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cor\":\"Preto\",\"placa\":\"ABC-1234\",\"nomeProprietario\":\"João Silva\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.modelo").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldHandleValidationException_MissingPlaca() throws Exception {
        // Act & Assert - Car without placa (required field)
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"modelo\":\"Honda Civic\",\"cor\":\"Preto\",\"nomeProprietario\":\"João Silva\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.placa").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldHandleValidationException_MultipleFields() throws Exception {
        // Act & Assert - Car without multiple required fields
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cor\":\"Preto\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.modelo").exists())
                .andExpect(jsonPath("$.errors.placa").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        // Arrange
        when(carService.getCarById(anyLong()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value(containsString("Erro interno do servidor")))
                .andExpect(jsonPath("$.message").value(containsString("Unexpected error")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldHandleResourceNotFoundException_ByPlaca() throws Exception {
        // Arrange
        when(carService.getCarByPlaca("XYZ-9999"))
                .thenThrow(new ResourceNotFoundException("Carro não encontrado com placa: XYZ-9999"));

        // Act & Assert
        mockMvc.perform(get("/api/cars/placa/XYZ-9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Carro não encontrado com placa: XYZ-9999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
