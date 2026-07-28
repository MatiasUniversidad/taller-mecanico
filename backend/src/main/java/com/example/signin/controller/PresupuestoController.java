package com.example.signin.controller;

import com.example.signin.dto.PresupuestoDTO;
import com.example.signin.model.Presupuesto;
import com.example.signin.service.PresupuestoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/presupuestos")
@CrossOrigin(origins = "*")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(
            PresupuestoService presupuestoService
    ) {
        this.presupuestoService = presupuestoService;
    }

    @GetMapping
    public ResponseEntity<List<Presupuesto>> listar() {
        return ResponseEntity.ok(
                presupuestoService.listarPresupuestos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(
            @PathVariable("id") Long id
    ) {
        try {
            return ResponseEntity.ok(
                    presupuestoService.obtenerPorId(id)
            );
        } catch (IllegalArgumentException exception) {
            return crearRespuestaError(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        } catch (RuntimeException exception) {
            return crearRespuestaError(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @GetMapping("/orden/{ordenTrabajoId}")
    public ResponseEntity<?> obtenerPorOrden(
            @PathVariable("ordenTrabajoId")
            Long ordenTrabajoId
    ) {
        try {
            return ResponseEntity.ok(
                    presupuestoService.obtenerPorOrden(
                            ordenTrabajoId
                    )
            );
        } catch (IllegalArgumentException exception) {
            return crearRespuestaError(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        } catch (RuntimeException exception) {
            return crearRespuestaError(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody PresupuestoDTO dto
    ) {
        try {
            Presupuesto presupuesto =
                    presupuestoService.crearPresupuesto(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(presupuesto);

        } catch (IllegalArgumentException exception) {
            return crearRespuestaError(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (IllegalStateException exception) {
            return crearRespuestaError(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            return crearRespuestaError(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @PathVariable("id") Long id
    ) {
        try {
            Presupuesto presupuesto =
                    presupuestoService.aprobarPresupuesto(id);

            return ResponseEntity.ok(presupuesto);

        } catch (IllegalArgumentException exception) {
            return crearRespuestaError(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (IllegalStateException exception) {
            return crearRespuestaError(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            return crearRespuestaError(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(
            @PathVariable("id") Long id
    ) {
        try {
            Presupuesto presupuesto =
                    presupuestoService.rechazarPresupuesto(id);

            return ResponseEntity.ok(presupuesto);

        } catch (IllegalArgumentException exception) {
            return crearRespuestaError(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (IllegalStateException exception) {
            return crearRespuestaError(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            return crearRespuestaError(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable("id") Long id
    ) {
        try {
            presupuestoService.eliminarPresupuesto(id);

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (IllegalArgumentException exception) {
            return crearRespuestaError(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );

        } catch (IllegalStateException exception) {
            return crearRespuestaError(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            return crearRespuestaError(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    private ResponseEntity<Map<String, String>>
    crearRespuestaError(
            HttpStatus estado,
            String mensaje
    ) {
        Map<String, String> respuesta =
                new HashMap<>();

        respuesta.put(
                "message",
                mensaje == null || mensaje.isBlank()
                        ? "Ocurrió un error al procesar la solicitud"
                        : mensaje
        );

        return ResponseEntity
                .status(estado)
                .body(respuesta);
    }
}