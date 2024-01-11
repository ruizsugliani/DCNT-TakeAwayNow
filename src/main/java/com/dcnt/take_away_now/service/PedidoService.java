package com.dcnt.take_away_now.service;

import com.dcnt.take_away_now.domain.*;
import com.dcnt.take_away_now.dto.InfoPedidoDto;
import com.dcnt.take_away_now.repository.*;
import com.dcnt.take_away_now.value_object.Dinero;
import com.dcnt.take_away_now.value_object.PuntosDeConfianza;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final NegocioRepository negocioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRegistroRepository inventarioRegistroRepository;
    private final ProductoPedidoRepository productoPedidoRepository;

    public Collection<Pedido> obtenerPedidos() {
        return pedidoRepository.findAll();
    }

    public boolean esUnClienteExistente(Long idCliente) {
        return clienteRepository.existsById(idCliente);
    }

    public boolean esUnNegocioExistente(Long idNegocio) {
        return negocioRepository.existsById(idNegocio);
    }

    public boolean esUnProductoExistente(Long idProducto) {
        return productoRepository.existsById(idProducto);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public boolean esUnProductoDeEseNegocio(Long idNegocio, Long idProducto) {
        Negocio negocio = negocioRepository.findById(idNegocio).get();
        Producto producto = productoRepository.findById(idProducto).get();
        return inventarioRegistroRepository.existsByNegocioAndProducto(negocio, producto);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public boolean sePuedeConfirmarUnPedidoParaEstosProductos(Map<Long, Integer> productos, Long idNegocio) {
        for (Map.Entry<Long, Integer> entry : productos.entrySet()) {
            if (!esUnProductoDeEseNegocio(idNegocio, entry.getKey())) {
                return false;
            }

            // Levantamos la cantidad pedida por el cliente y corroboramos que exista el stock necesario.
            Integer cantidadPedida = entry.getValue();
            Producto producto = productoRepository.findById(entry.getKey()).get();
            Negocio negocio = negocioRepository.findById(idNegocio).get();
            InventarioRegistro inventarioRegistro = inventarioRegistroRepository.findByNegocioAndProducto(negocio, producto).get();

            if (cantidadPedida > inventarioRegistro.getStock()) {
                return false;
            }

        }
        return true;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Transactional
    public ResponseEntity<HttpStatus> confirmarPedido(InfoPedidoDto dto) {
        // Dado que ya hemos corroborado todos los datos, procedemos a confirmar el pedido.
        Dinero precioTotalDelPedido = new Dinero(0);
        PuntosDeConfianza pdcTotalDelPedido = new PuntosDeConfianza(0);
        Negocio negocio = negocioRepository.findById(dto.getIdNegocio()).get();
        Cliente cliente = clienteRepository.findById(dto.getIdCliente()).get();
        Pedido pedido = new Pedido(negocio, cliente);

        pedidoRepository.save(pedido);

        for (Map.Entry<Long, Integer> entry : dto.getProductos().entrySet()) {
            Integer cantidadPedida = entry.getValue();
            Producto producto = productoRepository.findById(entry.getKey()).get();
            InventarioRegistro inventarioRegistro = inventarioRegistroRepository.findByNegocioAndProducto(negocio, producto).get();
            PuntosDeConfianza pdcProducto = inventarioRegistro.getRecompensaPuntosDeConfianza();

            inventarioRegistro.setStock(inventarioRegistro.getStock() - cantidadPedida);

            // Actualizamos el InventarioRegistro con el nuevo stock.
            inventarioRegistroRepository.save(inventarioRegistro);

            // Relacionamos el producto con el pedido.
            ProductoPedido productoPedido = new ProductoPedido(cantidadPedida, pedido, producto);
            productoPedidoRepository.save(productoPedido);

            // Aumentamos el precio del pedido en función de la cantidad de productos solicitados y repetimos la operación para los pdc.
            Dinero precioParcialPorProducto = new Dinero(cantidadPedida).multiply(inventarioRegistro.getPrecio());
            precioTotalDelPedido = precioTotalDelPedido.plus(precioParcialPorProducto);

            PuntosDeConfianza pdcParcialesPorProducto = inventarioRegistro.getRecompensaPuntosDeConfianza().multiply(cantidadPedida);
            pdcTotalDelPedido = pdcTotalDelPedido.plus(pdcParcialesPorProducto);
        }

        // Actualizamos el saldo y los puntos de confianza del cliente.
        cliente.setSaldo(cliente.getSaldo().minus(precioTotalDelPedido));
        cliente.setPuntosDeConfianza(cliente.getPuntosDeConfianza().plus(pdcTotalDelPedido));

        // Actualizamos el saldo del negocio.
        negocio.setSaldo(negocio.getSaldo().plus(precioTotalDelPedido));

        // Finalmente, guardamos el precio total del pedido.
        pedido.setPrecioTotal(precioTotalDelPedido);
        pedidoRepository.save(pedido);
        return  ResponseEntity.ok().build();
    }

    public ResponseEntity<HttpStatus> verificarPedido(InfoPedidoDto dto) {
        if (!esUnClienteExistente(dto.getIdCliente())) {
            return ResponseEntity.badRequest().build();
        }

        if (!esUnNegocioExistente(dto.getIdNegocio())) {
            return ResponseEntity.badRequest().build();
        }

        if (!sePuedeConfirmarUnPedidoParaEstosProductos(dto.getProductos(), dto.getIdNegocio())) {
            return ResponseEntity.badRequest().build();
        }

        return confirmarPedido(dto);
    }
}
