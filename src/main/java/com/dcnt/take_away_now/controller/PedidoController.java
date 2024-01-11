package com.dcnt.take_away_now.controller;

import com.dcnt.take_away_now.domain.Cliente;
import com.dcnt.take_away_now.domain.Pedido;
import com.dcnt.take_away_now.dto.InfoPedidoDto;
import com.dcnt.take_away_now.repository.ClienteRepository;
import com.dcnt.take_away_now.repository.NegocioRepository;
import com.dcnt.take_away_now.repository.PedidoRepository;
import com.dcnt.take_away_now.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final NegocioRepository negocioRepository;
    public PedidoController(PedidoRepository pedidoRepository, ClienteRepository clienteRepository, NegocioRepository negocioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.negocioRepository = negocioRepository;
    }

    /*****************
     *   Métodos Get *
     *****************/
    @GetMapping("/")
    public Collection<Pedido> obtenerPedidos() {
        return pedidoRepository.findAll();
    }

    /******************
     *   Métodos Post *
     ******************/
    @PostMapping("/")
    public ResponseEntity<org.apache.hc.core5.http.HttpStatus> confirmarPedido(@RequestBody InfoPedidoDto infoPedido) {
        if (!clienteRepository.existsById(infoPedido.getIdCliente())) {
            return ResponseEntity.notFound().build();
        }

        if (!negocioRepository.existsById(infoPedido.getIdNegocio())) {
            return ResponseEntity.notFound().build();
        }

        // TODO impactar en la base los productos en caso de existir y tener una cantidad mayor o igual a la solicitada.
        Map<Long, Integer> productos = infoPedido.getProductos();
        return ResponseEntity.ok().build();
    }

}
