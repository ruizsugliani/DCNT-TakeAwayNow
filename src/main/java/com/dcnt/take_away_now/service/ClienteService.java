package com.dcnt.take_away_now.service;

import com.dcnt.take_away_now.domain.Cliente;
import com.dcnt.take_away_now.domain.Pedido;
import com.dcnt.take_away_now.repository.ClienteRepository;
import com.dcnt.take_away_now.repository.PedidoRepository;
import com.dcnt.take_away_now.repository.ProductoPedidoRepository;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoPedidoRepository productoPedidoRepository;

    public Collection<Cliente> obtenerClientes() {
        return clienteRepository.findAll();
    }

    public ResponseEntity<HttpStatus> crearCliente(String usuario) {
        Optional<Cliente> optionalCliente = clienteRepository.findByUsuario(usuario);
        if (optionalCliente.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        this.clienteRepository.save(new Cliente(usuario));
        return ResponseEntity.ok().build();
    }

    public void eliminarCliente(Long idCliente) {
        clienteRepository.deleteById(idCliente);
    }
}
