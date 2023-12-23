package com.dcnt.take_away_now.service;

import com.dcnt.take_away_now.domain.Negocio;
import com.dcnt.take_away_now.domain.Producto;
import com.dcnt.take_away_now.domain.InventarioRegistro;
import com.dcnt.take_away_now.dto.InventarioRegistroDto;
import org.springframework.stereotype.Service;
import com.dcnt.take_away_now.repository.InventarioRegistroRepository;
import com.dcnt.take_away_now.repository.NegocioRepository;
import com.dcnt.take_away_now.repository.ProductoRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class NegocioService {
    private final NegocioRepository negocioRepository;
    private final InventarioRegistroRepository inventarioRegistroRepository;
    private final ProductoRepository productoRepository;


    public NegocioService(
            NegocioRepository negocioRepository,
            InventarioRegistroRepository inventarioRegistroRepository,
            ProductoRepository productoRepository
    ) {
        this.negocioRepository = negocioRepository;
        this.inventarioRegistroRepository = inventarioRegistroRepository;
        this.productoRepository = productoRepository;
    }

    public Negocio crearNegocio(
            String nombre,
            DayOfWeek diaDeApertura,
            DayOfWeek diaDeCierre
    ) {
        return this.negocioRepository.save(
                new Negocio(nombre, LocalTime.of(9,0), LocalTime.of(18,0), diaDeApertura, diaDeCierre)
        );
    }

    public Collection<Negocio> obtenerNegocios() {
        return negocioRepository.findAll();
    }

    public Producto crearProducto(
            Long negocioId,
            String nombreDelProducto,
            InventarioRegistroDto inventarioRegistroDto
    ) {
        // TODO Corroboramos que exista el negocio y ver qué devolver.
        Optional<Negocio> optionalNegocio = negocioRepository.findById(negocioId);
        if (optionalNegocio.isEmpty()) {
            return new Producto();
        }
        Negocio negocioExistente = optionalNegocio.get();
        // TODO Corroboramos que no exista un producto con el nombre pasado por parámetro para este negocio.



        // Finalmente, guardamos tanto el nuevo producto como la relación con el registro en la base de datos.
        Producto nuevoProducto = new Producto(nombreDelProducto);
        InventarioRegistro nuevoInventarioRegistro = new InventarioRegistro(inventarioRegistroDto);

        nuevoProducto.setInventarioRegistro(nuevoInventarioRegistro);
        productoRepository.save(nuevoProducto);

        negocioExistente.getInventarioRegistros().add(nuevoInventarioRegistro);
        negocioRepository.save(negocioExistente);

        nuevoInventarioRegistro.setProducto(nuevoProducto);
        nuevoInventarioRegistro.setNegocio(negocioExistente);
        inventarioRegistroRepository.save(nuevoInventarioRegistro);

        return nuevoProducto;
    }

}
