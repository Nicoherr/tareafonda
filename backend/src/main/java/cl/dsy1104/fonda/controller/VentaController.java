package cl.dsy1104.fonda.controller;

import cl.dsy1104.fonda.model.EstadoVenta;
import cl.dsy1104.fonda.model.Venta;
import cl.dsy1104.fonda.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> req) {
        Long bebidaId = Long.valueOf(req.get("bebidaId").toString());
        int unidades = Integer.parseInt(req.get("unidades").toString());

        Venta venta = ventaService.registrarVenta(bebidaId, unidades);

        if (venta.getEstado() == EstadoVenta.RECHAZADA) {
            Map<String, String> err = new HashMap<>();
            err.put("error", venta.getMotivo());
            err.put("mensaje", "La venta ha sido rechazada por motivo: " + venta.getMotivo());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(venta.getId()).toUri();
        return ResponseEntity.created(location).body(venta);
    }

    @GetMapping
    public ResponseEntity<List<Venta>> historial() {
        return ResponseEntity.ok(ventaService.historial());
    }
}
