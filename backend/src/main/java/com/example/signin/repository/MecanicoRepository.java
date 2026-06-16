package com.example.signin.repository;

import com.example.signin.model.Mecanico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MecanicoRepository extends JpaRepository<Mecanico, Integer> {

    Optional<Mecanico> findByRut(String rut);

    List<Mecanico> findByDisponible(Boolean disponible);

    List<Mecanico> findByEspecialidad(String especialidad);
}