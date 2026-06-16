package com.example.signin.repository;

import com.example.signin.model.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {

    Optional<Repuesto> findByCodigo(String codigo);

    List<Repuesto> findByStockLessThanEqual(Integer stock);
}