package com.parking.api.controller;

import com.parking.api.model.Car;
import com.parking.api.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    /**
     * GET /api/cars - Lista todos os carros
     */
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    /**
     * GET /api/cars/{id} - Busca um carro por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    /**
     * GET /api/cars/placa/{placa} - Busca um carro pela placa
     */
    @GetMapping("/placa/{placa}")
    public ResponseEntity<Car> getCarByPlaca(@PathVariable String placa) {
        Car car = carService.getCarByPlaca(placa);
        return ResponseEntity.ok(car);
    }

    /**
     * POST /api/cars - Registra entrada de um novo carro
     */
    @PostMapping
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {
        Car newCar = carService.createCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCar);
    }

    /**
     * PUT /api/cars/{id} - Atualiza informações de um carro
     */
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id,
                                         @Valid @RequestBody Car carDetails) {
        Car updatedCar = carService.updateCar(id, carDetails);
        return ResponseEntity.ok(updatedCar);
    }

    /**
     * PATCH /api/cars/{id}/exit - Registra saída de um carro
     */
    @PatchMapping("/{id}/exit")
    public ResponseEntity<Car> registerExit(@PathVariable Long id) {
        Car car = carService.registerExit(id);
        return ResponseEntity.ok(car);
    }

    /**
     * DELETE /api/cars/{id} - Remove um carro do registro
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
