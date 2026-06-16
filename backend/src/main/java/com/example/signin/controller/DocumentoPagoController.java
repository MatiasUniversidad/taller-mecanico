package com.example.signin.controller;

import com.example.signin.dto.DocumentoPagoDTO;
import com.example.signin.model.DocumentoPago;
import com.example.signin.service.DocumentoPagoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos-pago")
@CrossOrigin(origins = "*")
public class DocumentoPagoController {

    private final DocumentoPagoService documentoPagoService;

    public DocumentoPagoController(DocumentoPagoService documentoPagoService) {
        this.documentoPagoService = documentoPagoService;
    }

    @GetMapping
    public List<DocumentoPago> listarDocumentos() {
        return documentoPagoService.listarDocumentos();
    }

    @GetMapping("/{id}")
    public DocumentoPago obtenerPorId(@PathVariable("id") Long id) {
        return documentoPagoService.obtenerPorId(id);
    }

    @GetMapping("/presupuesto/{presupuestoId}")
    public DocumentoPago obtenerPorPresupuesto(
            @PathVariable("presupuestoId") Long presupuestoId
    ) {
        return documentoPagoService.obtenerPorPresupuesto(presupuestoId);
    }

    @GetMapping("/orden/{ordenTrabajoId}")
    public DocumentoPago obtenerPorOrden(
            @PathVariable("ordenTrabajoId") Long ordenTrabajoId
    ) {
        return documentoPagoService.obtenerPorOrden(ordenTrabajoId);
    }

    @PostMapping
    public DocumentoPago crearDocumento(@RequestBody DocumentoPagoDTO dto) {
        return documentoPagoService.crearDocumento(dto);
    }

    @DeleteMapping("/{id}")
    public void eliminarDocumento(@PathVariable("id") Long id) {
        documentoPagoService.eliminarDocumento(id);
    }
}