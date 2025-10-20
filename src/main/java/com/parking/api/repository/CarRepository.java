package com.parking.api.repository;

import com.parking.api.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByPlaca(String placa);

    boolean existsByPlaca(String placa);
}
