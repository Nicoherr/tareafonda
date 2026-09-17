package cl.dsy1104.fonda.service;

import cl.dsy1104.fonda.model.Bebida;
import cl.dsy1104.fonda.model.TipoBebida;
import cl.dsy1104.fonda.repository.BebidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BebidaService {

    @Autowired
    private BebidaRepository bebidaRepository;

    public int calcularPrecio(Bebida bebida) {
        if (bebida.getTipo() == TipoBebida.ALCOHOLICA) {
            int precio = 3500;
            if (Boolean.FALSE.equals(bebida.getCertificada())) {
                precio *= 1.20; // 20% recargo si no está certificada
            }
            return precio;
        } else {
            int precio = 2000;
            if (bebida.getAzucarPorLitro() != null && bebida.getAzucarPorLitro() > 80) {
                precio *= 1.10; // 10% recargo si azucar > 80 g/L
            }
            return precio;
        }
    }

    public List<Bebida> listar(String nombre) {
        if (nombre != null && !nombre.isBlank()) {
            return bebidaRepository.findByNombreContainingIgnoreCase(nombre);
        }
        return bebidaRepository.findAll();
    }

    public Optional<Bebida> obtenerPorId(Long id) {
        return bebidaRepository.findById(id);
    }

    public Bebida guardar(Bebida bebida) {
        return bebidaRepository.save(bebida);
    }

    public void eliminar(Long id) {
        bebidaRepository.deleteById(id);
    }
}