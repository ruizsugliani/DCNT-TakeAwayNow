package domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import value_object.Dinero;
import value_object.PuntosDeConfianza;
import value_object.converter.DineroAttributeConverter;
import value_object.converter.PuntosDeConfianzaAttributeConverter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "INVENTARIO_REGISTROS")
public class InventarioRegistro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INVENTARIO_REGISTRO")
    private Long id;

    @ManyToOne(targetEntity = Negocio.class)
    @JoinColumn(name = "ID_NEGOCIO")
    private Negocio negocio;

    @OneToOne(targetEntity = Producto.class,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ID_PRODUCTO")
    private Producto producto;

    @Column(name="STOCK")
    private Long Stock;

    @Column(name="PRECIO_UNITARIO")
    @Convert(converter = DineroAttributeConverter.class)
    private Dinero precio;

    @Column(name="RECOMPENSA_PDC")
    @Convert(converter = PuntosDeConfianzaAttributeConverter.class)
    private PuntosDeConfianza recompensaPuntosDeConfianza;
}
