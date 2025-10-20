package com.parking.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Modelo é obrigatório")
    @Column(nullable = false)
    private String modelo;

    @NotBlank(message = "Cor é obrigatória")
    @Column(nullable = false)
    private String cor;

    @NotBlank(message = "Placa é obrigatória")
    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @NotBlank(message = "Nome do proprietário é obrigatório")
    @Column(nullable = false)
    private String nomeProprietario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataEntrada;

    @Column
    private LocalDateTime dataSaida;

    @PrePersist
    protected void onCreate() {
        dataEntrada = LocalDateTime.now();
    }
}
