package com.example.signin.service;

import com.example.signin.dto.OrdenTrabajoDTO;
import com.example.signin.model.Mecanico;
import com.example.signin.model.OrdenTrabajo;
import com.example.signin.model.Vehiculo;
import com.example.signin.repository.MecanicoRepository;
import com.example.signin.repository.OrdenTrabajoRepository;
import com.example.signin.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final MecanicoRepository mecanicoRepository;

    public OrdenTrabajoService(
            OrdenTrabajoRepository ordenTrabajoRepository,
            VehiculoRepository vehiculoRepository,
            MecanicoRepository mecanicoRepository
    ) {
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.mecanicoRepository = mecanicoRepository;
    }

    public List<OrdenTrabajo> listarOrdenes() {
        return ordenTrabajoRepository.findAll();
    }

    public OrdenTrabajo crearOrden(OrdenTrabajoDTO dto) {

        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        OrdenTrabajo orden = new OrdenTrabajo();

        orden.setEstado(dto.getEstado());
        orden.setDiagnosticoPreliminar(dto.getDiagnosticoPreliminar());
        orden.setVehiculo(vehiculo);

        return ordenTrabajoRepository.save(orden);
    }

    public List<OrdenTrabajo> obtenerPorVehiculo(Integer vehiculoId) {
        return ordenTrabajoRepository.findByVehiculoId(vehiculoId);
    }

    public List<OrdenTrabajo> obtenerPorEstado(String estado) {
        return ordenTrabajoRepository.findByEstado(estado);
    }

    public OrdenTrabajo obtenerPorId(Long id) {
        return ordenTrabajoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    public OrdenTrabajo asignarMecanico(Long ordenId, Integer mecanicoId) {

        OrdenTrabajo orden = ordenTrabajoRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        Mecanico mecanico = mecanicoRepository.findById(mecanicoId)
                .orElseThrow(() -> new RuntimeException("Mecánico no encontrado"));

        if (!mecanico.getDisponible()) {
            throw new RuntimeException("El mecánico no está disponible");
        }

        orden.setMecanico(mecanico);
        orden.setEstado("ASIGNADA");

        mecanico.setDisponible(false);
        mecanicoRepository.save(mecanico);

        return ordenTrabajoRepository.save(orden);
    }

    public void eliminar(Long id) {
        ordenTrabajoRepository.deleteById(id);
    }
}