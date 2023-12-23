package com.dcnt.take_away_now.controller;

import com.dcnt.take_away_now.domain.Negocio;
import com.dcnt.take_away_now.domain.Producto;
import com.dcnt.take_away_now.dto.InventarioRegistroDto;
import com.dcnt.take_away_now.value_object.Dinero;
import com.dcnt.take_away_now.value_object.PuntosDeConfianza;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.dcnt.take_away_now.service.NegocioService;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/negocios")
public class NegocioController {
    private final NegocioService negocioService;

    public NegocioController(NegocioService negocioService) {
        this.negocioService = negocioService;
    }

    @GetMapping("/")
    public Collection<Negocio> obtenerNegocios() {
        return negocioService.obtenerNegocios();
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Negocio crearNegocio(
            @RequestParam String nombre,
            @RequestParam DayOfWeek diaDeApertura,
            @RequestParam DayOfWeek diaDeCierre
            ) {
        return negocioService.crearNegocio(nombre, diaDeApertura, diaDeCierre);
    }

    @PostMapping("/{negocioId}/productos/")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(
            @PathVariable Long negocioId,
            @RequestParam String nombreDelProducto,
            @RequestParam Long stock,
            @RequestParam BigDecimal precio,
            @RequestParam Integer recompensaPuntosDeConfianza
    ) {
        return negocioService.crearProducto(negocioId, nombreDelProducto, new InventarioRegistroDto(stock, new Dinero(precio), new PuntosDeConfianza(recompensaPuntosDeConfianza)));
    }
}
