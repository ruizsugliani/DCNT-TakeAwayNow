package service;

import domain.Negocio;
import domain.Producto;
import domain.InventarioRegistro;
import dto.InventarioRegistroDto;
import org.springframework.stereotype.Service;
import repository.InventarioRegistroRepository;
import repository.NegocioRepository;
import repository.ProductoRepository;

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
            LocalTime horarioDeApertura,
            LocalTime horarioDeCierre,
            DayOfWeek diaDeApertura,
            DayOfWeek diaDeCierre
    ) {
        return this.negocioRepository.save(
                new Negocio(nombre, horarioDeApertura, horarioDeCierre, diaDeApertura, diaDeCierre)
        );
    }

    public Collection<Negocio> obtenerNegocios() {
        return negocioRepository.findAll();
    }

    public void crearProducto(
            Long negocioId,
            String nombreDelProducto,
            InventarioRegistroDto inventarioRegistroDto
    ) {
        // TODO Corroboramos que exista el negocio y ver qué devolver.
        Optional<Negocio> optionalNegocio = negocioRepository.findById(negocioId);
        if (optionalNegocio.isEmpty()) {
            return;
        }
        Negocio negocioExistente = optionalNegocio.get();
        // TODO Corroboramos que no exista un producto con el nombre pasado por parámetro para este negocio.



        // Finalmente, guardamos tanto el nuevo producto como la relación con el registro en la base de datos.
        Producto nuevoProducto = new Producto(nombreDelProducto);
        InventarioRegistro nuevoInventarioRegistro = new InventarioRegistro(inventarioRegistroDto);

        nuevoProducto.setInventarioRegistro(nuevoInventarioRegistro);
        nuevoInventarioRegistro.setProducto(nuevoProducto);
        nuevoInventarioRegistro.setNegocio(negocioExistente);
        negocioExistente.getInventarioRegistros().add(nuevoInventarioRegistro);

        productoRepository.save(nuevoProducto);
        inventarioRegistroRepository.save(nuevoInventarioRegistro);
        negocioRepository.save(negocioExistente);
    }
}
