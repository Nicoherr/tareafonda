package cl.dsy1104.fonda.controller;

import cl.dsy1104.fonda.model.Bebida;
import cl.dsy1104.fonda.service.BebidaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/bebidas")
public class BebidaController {

    @Autowired
    private BebidaService bebidaService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar(@RequestParam(required = false) String nombre) {
        List<Bebida> bebidas = bebidaService.listar(nombre);
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Bebida b : bebidas) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", b.getId());
            dto.put("nombre", b.getNombre());
            dto.put("tipo", b.getTipo());
            dto.put("volumenML", b.getVolumenML());
            dto.put("stock", b.getStock());
            dto.put("gradosAlcohol", b.getGradosAlcohol());
            dto.put("certificada", b.getCertificada());
            dto.put("azucarPorLitro", b.getAzucarPorLitro());
            dto.put("ventaRestringida", b.isVentaRestringida());
            dto.put("precio", bebidaService.calcularPrecio(b));
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bebida> obtenerPorId(@PathVariable Long id) {
        return bebidaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Bebida bebida) {
        Bebida creada = bebidaService.guardar(bebida);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Bebida bebida) {
        if (bebidaService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        bebida.setId(id);
        Bebida actualizada = bebidaService.guardar(bebida);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (bebidaService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        bebidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restriccion")
    public ResponseEntity<Bebida> cambiarRestriccion(@PathVariable Long id) {
        Optional<Bebida> opt = bebidaService.obtenerPorId(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Bebida bebida = opt.get();
        bebida.setVentaRestringida(!bebida.isVentaRestringida());
        bebidaService.guardar(bebida);
        return ResponseEntity.ok(bebida);
    }
}