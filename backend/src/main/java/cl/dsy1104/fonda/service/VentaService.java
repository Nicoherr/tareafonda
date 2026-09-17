package cl.dsy1104.fonda.service;

import cl.dsy1104.fonda.model.*;
import cl.dsy1104.fonda.repository.BebidaRepository;
import cl.dsy1104.fonda.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final BebidaRepository bebidaRepository;
    private final BebidaService bebidaService;

    @Value("${fonda.limite-unidades-por-cliente:3}")
    private int limiteUnidadesPorCliente;

    public VentaService(VentaRepository vRepo, BebidaRepository bRepo, BebidaService bService) {
        this.ventaRepository = vRepo;
        this.bebidaRepository = bRepo;
        this.bebidaService = bService;
    }

    public Venta registrarVenta(Long bebidaId, int unidades) {
        Bebida bebida = bebidaRepository.findById(bebidaId)
                .orElseThrow(() -> new IllegalArgumentException("Bebida no encontrada"));

        Venta venta = new Venta();
        venta.setBebida(bebida);
        venta.setUnidades(unidades);

        // 1. Verificación: Venta restringida
        if (bebida.isVentaRestringida()) {
            venta.setEstado(EstadoVenta.RECHAZADA);
            venta.setMotivo("VENTA_RESTRINGIDA");
            return ventaRepository.save(venta);
        }

        // 2. Verificación: Límite de alcohol
        if (bebida.getTipo() == TipoBebida.ALCOHOLICA && unidades > limiteUnidadesPorCliente) {
            venta.setEstado(EstadoVenta.RECHAZADA);
            venta.setMotivo("LIMITE_EXCEDIDO");
            return ventaRepository.save(venta);
        }

        // 3. Verificación: Stock
        if (bebida.getStock() < unidades) {
            venta.setEstado(EstadoVenta.RECHAZADA);
            venta.setMotivo("STOCK_INSUFICIENTE");
            return ventaRepository.save(venta);
        }

        // 4. Autorización
        int precioUnitario = bebidaService.calcularPrecio(bebida);
        venta.setTotal(precioUnitario * unidades);
        venta.setEstado(EstadoVenta.AUTORIZADA);

        // Descontar Stock
        bebida.setStock(bebida.getStock() - unidades);
        bebidaRepository.save(bebida);

        return ventaRepository.save(venta);
    }

    public List<Venta> historial() {
        return ventaRepository.findAll();
    }
}