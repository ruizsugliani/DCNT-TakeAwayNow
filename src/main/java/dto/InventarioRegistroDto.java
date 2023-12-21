package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import value_object.Dinero;
import value_object.PuntosDeConfianza;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InventarioRegistroDto {
    private Long Stock;
    private Dinero precio;
    private PuntosDeConfianza recompensaPuntosDeConfianza;
}
