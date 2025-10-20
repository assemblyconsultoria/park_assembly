package com.parking.api.service;

import com.parking.api.exception.DuplicatePlacaException;
import com.parking.api.exception.ResourceNotFoundException;
import com.parking.api.model.Car;
import com.parking.api.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    /**
     * Lista todos os carros estacionados
     */
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    /**
     * Busca um carro por ID
     */
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carro não encontrado com ID: " + id));
    }

    /**
     * Busca um carro pela placa
     */
    public Car getCarByPlaca(String placa) {
        return carRepository.findByPlaca(placa)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carro não encontrado com placa: " + placa));
    }

    /**
     * Registra entrada de um carro no estacionamento
     */
    @Transactional
    public Car createCar(Car car) {
        // Verifica se já existe um carro com a mesma placa
        if (carRepository.existsByPlaca(car.getPlaca())) {
            throw new DuplicatePlacaException(
                    "Já existe um carro registrado com a placa: " + car.getPlaca());
        }

        return carRepository.save(car);
    }

    /**
     * Atualiza informações de um carro
     */
    @Transactional
    public Car updateCar(Long id, Car carDetails) {
        Car car = getCarById(id);

        // Verifica se a placa foi alterada e se já existe outro carro com a nova placa
        if (!car.getPlaca().equals(carDetails.getPlaca()) &&
            carRepository.existsByPlaca(carDetails.getPlaca())) {
            throw new DuplicatePlacaException(
                    "Já existe um carro registrado com a placa: " + carDetails.getPlaca());
        }

        car.setModelo(carDetails.getModelo());
        car.setCor(carDetails.getCor());
        car.setPlaca(carDetails.getPlaca());
        car.setNomeProprietario(carDetails.getNomeProprietario());

        return carRepository.save(car);
    }

    /**
     * Registra saída de um carro do estacionamento
     */
    @Transactional
    public Car registerExit(Long id) {
        Car car = getCarById(id);
        car.setDataSaida(LocalDateTime.now());
        return carRepository.save(car);
    }

    /**
     * Remove um carro do registro
     */
    @Transactional
    public void deleteCar(Long id) {
        Car car = getCarById(id);
        carRepository.delete(car);
    }
}
