package com.dcnt.take_away_now.value_object;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PuntosDeConfianza implements Comparable<PuntosDeConfianza> {
    public int cantidad;

    public int toInt() {
        return this.getCantidad();
    }
    public PuntosDeConfianza(int cantidadInicial) {
        if (cantidadInicial < 0) {
            throw new IllegalStateException("No se pueden crear Puntos de Confianza con una cantidad menor a cero.");
        }
        this.cantidad = cantidadInicial;
    }
    /**
     * Compara la cantidad actual con la cantidad de puntos de confianza recibidos
     */
    public int compareTo(PuntosDeConfianza otro) {
        return Integer.compare(this.getCantidad(), otro.getCantidad());
    }

    /**
     *
     * Suma puntos de confianza con un entero. Retorna puntos de confianza
     * con la cantidad restultante de sumar la actual con la indicada. Si la cantidad
     * indicada es negativa se lanza un error.
     *
     */
    public PuntosDeConfianza plus(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalStateException("La cantidad a agregar no puede ser negativa o cero");
        }
        return new PuntosDeConfianza(this.cantidad + cantidad);
    }

    /**
     *
     * Resta puntos de confianza con un entero. Retorna puntos de confianza
     * con la cantidad restultante de restar la actual con la indicada por parametro.
     * Si resta da menor a cero se lanza un error.
     * Si la cantidad indicada por parametro es negativa se lanza un error.
     */
    public PuntosDeConfianza minus(int cantidadPorRestar) {
        if (cantidadPorRestar < 0) {
            throw new IllegalStateException("La cantidad de puntos de confianza a restar no puede ser negativa.");
        }
        if (this.cantidad - cantidadPorRestar < 0) {
            throw new IllegalStateException("La cantidad de puntos de confianza tras una resta no puede ser negativa.");
        }
        return new PuntosDeConfianza(this.cantidad - cantidadPorRestar);
    }

    /**
     *
     * Suma la cantidad actual con la cantidad de los puntos de confianza recibidos y retorna
     * nuevos puntos de confianza con el valor resultante
     *
     */
    public PuntosDeConfianza plus(PuntosDeConfianza otro) {
        return new PuntosDeConfianza(this.cantidad + otro.cantidad);
    }

    /**
     *
     * Resta la cantidad actual con la cantidad de puntos de confianza recibidos y retorna
     * nuevos puntos de confianza con el valor resultante.
     * Si resta da menor a cero se lanza un error.
     * Si la cantidad indicada por parametro es negativa se lanza un error.
     */
    public PuntosDeConfianza minus(PuntosDeConfianza puntosPorRestar) {
        if (puntosPorRestar.getCantidad() < 0) {
            throw new IllegalStateException("La cantidad de puntos de confianza a restar no puede ser negativa.");
        }
        if (this.getCantidad() - puntosPorRestar.getCantidad() < 0) {
            throw new IllegalStateException("La cantidad de puntos de confianza tras una resta no puede ser negativa.");
        }
        return new PuntosDeConfianza(this.getCantidad() - puntosPorRestar.getCantidad());
    }

    /**
     *
     * Multiplica la cantidad actual por la cantidad indicada por parámetro.
     * Retorna una instancia de PuntosDeConfianza con la cantidad resultante.
     * Si esta cantidad indicada por parámetro es negativa se lanza un error.
     */
    public PuntosDeConfianza multiply(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalStateException("No se pueden multiplicar puntos de confianza por números menores a cero.");
        }
        return new PuntosDeConfianza(this.cantidad * cantidad);
    }

    /*
    PuntosDeConfianza eliminarPuntosPorCompra(Compra compra, int multiplicador = 1) {
        this - this.calcularPuntosPorCompra(compra) * multiplicador
    }

    PuntosDeConfianza agregarPuntosPorCompra(Compra compra, int multiplicador = 1) {
        this + this.calcularPuntosPorCompra(compra) * multiplicador
    }


    PuntosDeConfianza calcularPuntosPorCompra(Compra compra) {
        new PuntosDeConfianza(compra.cantidadDeProductosPorDinero())
    }
    */
}
