package com.dcnt.take_away_now.controller;

import com.dcnt.take_away_now.domain.Negocio;
import com.dcnt.take_away_now.dto.InventarioRegistroDto;
import com.dcnt.take_away_now.dto.ProductoDto;
import com.dcnt.take_away_now.service.NegocioService;
import com.dcnt.take_away_now.value_object.Dinero;
import com.dcnt.take_away_now.value_object.PuntosDeConfianza;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Collection;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/negocios")
public class NegocioController {
    private final NegocioService negocioService;

    public NegocioController(NegocioService negocioService) {
        this.negocioService = negocioService;
    }

    @GetMapping("/{negocioId}/productos")
    public Collection<ProductoDto> obtenerProductos(@PathVariable Long negocioId) {
        return negocioService.obtenerProductos(negocioId);
    }

    @GetMapping("/")
    public Collection<Negocio> obtenerNegocios() {
        return negocioService.obtenerNegocios();
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<org.apache.hc.core5.http.HttpStatus> crearNegocio(
            @RequestParam String nombre,
            @RequestParam DayOfWeek diaDeApertura,
            @RequestParam DayOfWeek diaDeCierre
    ) {
        return negocioService.crearNegocio(nombre, diaDeApertura, diaDeCierre);
    }

    @PostMapping("/{negocioId}/productos/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<org.apache.hc.core5.http.HttpStatus> crearProducto(
            @PathVariable Long negocioId,
            @RequestParam String nombreDelProducto,
            @RequestParam Long stock,
            @RequestParam BigDecimal precio,
            @RequestParam Integer recompensaPuntosDeConfianza
    ) {
        return negocioService.crearProducto(negocioId, nombreDelProducto, new InventarioRegistroDto(stock, new Dinero(precio), new PuntosDeConfianza(recompensaPuntosDeConfianza)));
    }

    @DeleteMapping("/{negocioId}/productos/{productoId}")
    public ResponseEntity<org.apache.hc.core5.http.HttpStatus> eliminarProducto(
            @PathVariable Long negocioId,
            @PathVariable Long productoId
    ) {
        return negocioService.eliminarProducto(negocioId, productoId);
    }
}
