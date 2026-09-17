package cl.dsy1104.fonda.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Bebida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoBebida tipo;

    private int volumenML;
    private int stock;
    private Double gradosAlcohol;
    private Boolean certificada;
    private Integer azucarPorLitro;
    private boolean ventaRestringida;
}