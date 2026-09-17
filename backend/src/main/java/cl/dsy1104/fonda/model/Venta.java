package cl.dsy1104.fonda.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bebida_id")
    private Bebida bebida;

    private int unidades;
    private int total;

    @Enumerated(EnumType.STRING)
    private EstadoVenta estado;

    private String motivo;
    private LocalDateTime fecha = LocalDateTime.now();
}