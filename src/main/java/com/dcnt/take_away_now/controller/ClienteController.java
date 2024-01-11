package com.dcnt.take_away_now.controller;

import com.dcnt.take_away_now.domain.Cliente;
import com.dcnt.take_away_now.service.ClienteService;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /*****************
     *   Métodos Get *
     *****************/
    @GetMapping("/")
    public Collection<Cliente> obtenerClientes() {
        return clienteService.obtenerClientes();
    }

    /******************
     *   Métodos Post *
     ******************/
    @PostMapping("/")
    public ResponseEntity<HttpStatus> crearCliente(@RequestBody String nombreUsuario) {
        return clienteService.crearCliente(nombreUsuario);
    }

    /*******************
     *   Métodos Patch *
     *******************/

    /*******************
    *   Métodos Delete *
    ********************/
    @DeleteMapping("/{clienteId}")
    public void eliminarCliente(@PathVariable Long clienteId) {
        clienteService.eliminarCliente(clienteId);
    }
}
