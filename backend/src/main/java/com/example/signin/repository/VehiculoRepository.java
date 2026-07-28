package com.example.signin.repository;

import com.example.signin.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    List<Vehiculo> findByClienteId(Integer clienteId);

    Optional<Vehiculo> findByPatente(String patente);
}