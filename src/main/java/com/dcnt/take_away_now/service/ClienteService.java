package com.dcnt.take_away_now.service;

import com.dcnt.take_away_now.domain.Cliente;
import com.dcnt.take_away_now.domain.Pedido;
import com.dcnt.take_away_now.dto.PedidoDto;
import com.dcnt.take_away_now.enums.EstadoDelPedido;
import com.dcnt.take_away_now.repository.ClienteRepository;
import com.dcnt.take_away_now.repository.PedidoRepository;
import com.dcnt.take_away_now.repository.ProductoPedidoRepository;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public Collection<Cliente> obtenerClientes() {
        return clienteRepository.findAll();
    }

    public ResponseEntity<HttpStatus> crearCliente(String usuario) {
        Optional<Cliente> optionalCliente = clienteRepository.findByUsuario(usuario);
        if (optionalCliente.isPresent()) {
            throw new RuntimeException("Ya existe un usuario con el nombre ingresado.");
        }

        this.clienteRepository.save(new Cliente(usuario));
        return ResponseEntity.ok().build();
    }

    public void eliminarCliente(Long idCliente) {
        clienteRepository.deleteById(idCliente);
    }

    public Collection<PedidoDto> obtenerPedidos(Long idCliente) {
        // Corroboramos la existencia del cliente
        clienteRepository.findById(idCliente).orElseThrow( () -> new RuntimeException("No existe el cliente en la base de datos.") );

        return pedidoRepository.obtenerPedidosDelCliente(idCliente);
    }

    public ResponseEntity<HttpStatus> confirmarRetiroDelPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow( () -> new RuntimeException("No existe el pedido al cual usted quiere confirmar el retiro."));
        if (pedido.estado != EstadoDelPedido.LISTO_PARA_RETIRAR) {
            throw new IllegalStateException("No se puede retirar dicho pedido ya que el mismo no se encuentra listo para retirar.");
        }
        pedido.setEstado(EstadoDelPedido.RETIRADO);
        pedidoRepository.save(pedido);
        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<HttpStatus> cancelarPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow( () -> new RuntimeException("No existe el pedido que usted busca cancelar."));
        if (pedido.estado != EstadoDelPedido.RETIRADO) {
            throw new IllegalStateException("No se puede cancelar dicho pedido ya que el mismo no se encontraba retirado.");
        }
        pedido.setEstado(EstadoDelPedido.CANCELADO);
        pedidoRepository.save(pedido);
        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<HttpStatus> devolverPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow( () -> new RuntimeException("No existe el pedido que usted busca devolver."));
        if (pedido.estado != EstadoDelPedido.RETIRADO) {
            throw new IllegalStateException("No se puede devolver dicho pedido ya que el mismo no se encontraba retirado.");
        }
        pedido.setEstado(EstadoDelPedido.DEVUELTO);
        pedidoRepository.save(pedido);
        return ResponseEntity.accepted().build();
    }
}
