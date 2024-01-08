package com.dcnt.take_away_now.service;

import com.dcnt.take_away_now.domain.Negocio;
import com.dcnt.take_away_now.domain.Producto;
import com.dcnt.take_away_now.dto.InventarioRegistroDto;
import com.dcnt.take_away_now.dto.ProductoDto;
import com.dcnt.take_away_now.repository.InventarioRegistroRepository;
import com.dcnt.take_away_now.repository.NegocioRepository;
import com.dcnt.take_away_now.repository.ProductoRepository;
import com.dcnt.take_away_now.value_object.Dinero;
import com.dcnt.take_away_now.value_object.PuntosDeConfianza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NegocioServiceTest {
    @Mock
    private NegocioRepository negocioRepository;
    @Mock
    private InventarioRegistroRepository inventarioRegistroRepository;
    @Mock
    private ProductoRepository productoRepository;
    private NegocioService negocioService;
    private DayOfWeek DiaDeApertura;
    private DayOfWeek DiaDeCierre;
    @BeforeEach
    void setUp() {
        DiaDeApertura = DayOfWeek.MONDAY;
        DiaDeCierre = DayOfWeek.FRIDAY;
        negocioService = new NegocioService(negocioRepository, inventarioRegistroRepository, productoRepository);
    }

    @Test
    void crearNegocio() {
        //when
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre);
        Optional<Negocio> negocio = negocioRepository.findByNombre("Buffet Paseo Colon");
        //then
        assertThat(negocio.isPresent()).isTrue();
    }

    @Test
    void obtenerNegocios() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre);
        negocioService.crearNegocio("Buffet Las Heras", DiaDeApertura, DiaDeCierre);
        negocioService.crearNegocio("Buffet Ciudad Universitaria", DiaDeApertura, DiaDeCierre);
        //when
        Collection<Negocio> negocios = negocioService.obtenerNegocios();
        //then
        assertThat(negocios.size()).isEqualTo(3);
    }

    @Test
    void crearProducto() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre);
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20));
        Optional<Negocio> negocio = negocioRepository.findByNombre("Buffet Paseo Colon");
        //when
        negocioService.crearProducto(negocio.get().getId(), "Pancho",inventarioRegistroDto);
        //then
        Optional<Producto> producto = productoRepository.findByNombre("Pancho");
        boolean existeInventarioRegistro = inventarioRegistroRepository.findByNegocioAndProducto(negocio.get(), producto.get()).isPresent();
        assertThat(producto).isPresent();
        assertThat(existeInventarioRegistro).isTrue();
    }

    @Test
    void eliminarProducto() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre);
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20));
        Optional<Negocio> negocio = negocioRepository.findByNombre("Buffet Paseo Colon");
        negocioService.crearProducto(negocio.get().getId(), "Pancho",inventarioRegistroDto);
        Optional<Producto> producto = productoRepository.findByNombre("Pancho");
        //when
        negocioService.eliminarProducto(negocio.get().getId(), producto.get().getId());
        //then
        boolean existeInventarioRegistro = inventarioRegistroRepository.findByNegocioAndProducto(negocio.get(), producto.get()).isPresent();
        boolean existeProducto = productoRepository.findByNombre("Pancho").isPresent();
        assertThat(existeInventarioRegistro).isFalse();
        assertThat(existeProducto).isFalse();

    }

    @Test
    void obtenerProductos() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre);
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20));
        Optional<Negocio> negocio = negocioRepository.findByNombre("Buffet Paseo Colon");
        negocioService.crearProducto(negocio.get().getId(), "Pancho",inventarioRegistroDto);
        negocioService.crearProducto(negocio.get().getId(), "Coca Cola",inventarioRegistroDto);
        negocioService.crearProducto(negocio.get().getId(), "Alfajor",inventarioRegistroDto);
        //when
        Collection<ProductoDto> productos = negocioService.obtenerProductos(negocio.get().getId());
        //then
        assertThat(productos.size()).isEqualTo(3);
    }
}