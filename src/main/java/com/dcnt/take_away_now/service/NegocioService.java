package com.dcnt.take_away_now.service;

import com.dcnt.take_away_now.domain.InventarioRegistro;
import com.dcnt.take_away_now.domain.Negocio;
import com.dcnt.take_away_now.domain.Producto;
import com.dcnt.take_away_now.dto.InventarioRegistroDto;
import com.dcnt.take_away_now.repository.InventarioRegistroRepository;
import com.dcnt.take_away_now.repository.NegocioRepository;
import com.dcnt.take_away_now.repository.ProductoRepository;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<HttpStatus> crearProducto(
            Long negocioId,
            String nombreDelProducto,
            InventarioRegistroDto inventarioRegistroDto
    ) {
        // Corroboramos que exista el negocio.
        Optional<Negocio> optionalNegocio = negocioRepository.findById(negocioId);
        if (optionalNegocio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Corroboramos que no exista un producto con el nombre pasado por parámetro para este negocio.
        Optional<Producto> optionalProducto = productoRepository.findByNombre(nombreDelProducto);
        if (optionalProducto.isPresent() && inventarioRegistroRepository.existsByNegocioAndProducto(optionalNegocio.get(), optionalProducto.get())) {
            return ResponseEntity.internalServerError().build();
        }

        // Creamos el nuevo producto y el registro.
        InventarioRegistro nuevoInventarioRegistro = inventarioRegistroRepository.save(new InventarioRegistro(inventarioRegistroDto));
        Producto nuevoProducto = productoRepository.save(new Producto(nombreDelProducto));
        Negocio negocioExistente = optionalNegocio.get();

        nuevoInventarioRegistro.setProducto(nuevoProducto);
        nuevoInventarioRegistro.setNegocio(negocioExistente);
        nuevoProducto.setInventarioRegistro(nuevoInventarioRegistro);
        //negocioExistente.registrarProductoEnInventario(nuevoInventarioRegistro);

        //negocioRepository.save(negocioExistente);
        productoRepository.save(nuevoProducto);
        inventarioRegistroRepository.save(nuevoInventarioRegistro);

        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<HttpStatus> eliminarProducto(Long negocioId, Long productoId) {
        Optional<Negocio> OptNegocio = negocioRepository.findById(negocioId);
        Optional<Producto> OptProducto = productoRepository.findById(productoId);

        // Corroboramos que dichos IDs pertenezcan a registros existentes en la DB.
        if (OptNegocio.isEmpty() || OptProducto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<InventarioRegistro> OptInventarioRegistro = inventarioRegistroRepository.findByNegocioAndProducto(OptNegocio.get(), OptProducto.get());
        // Corroboramos que exista la relación entre el negocio y el producto en cuestión.
        if (OptInventarioRegistro.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Borramos la relación de la tabla de correlación.
        OptProducto.get().setInventarioRegistro(null);
        OptNegocio.get().getInventarioRegistros().remove(OptInventarioRegistro.get());
        inventarioRegistroRepository.deleteByNegocioAndProducto(OptNegocio.get(), OptProducto.get());
        // Borramos el producto de su respectiva tabla.
        productoRepository.deleteById(OptProducto.get().getId());
        return ResponseEntity.ok().build();
    }
}
