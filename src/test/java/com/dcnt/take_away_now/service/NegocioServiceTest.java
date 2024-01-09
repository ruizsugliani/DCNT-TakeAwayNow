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
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//@ExtendWith(MockitoExtension.class)
@DataJpaTest
class NegocioServiceTest {
    @Autowired
    private NegocioRepository negocioRepository;
    @Autowired
    private InventarioRegistroRepository inventarioRegistroRepository;
    @Autowired
    private ProductoRepository productoRepository;
    private NegocioService negocioService;
    private DayOfWeek DiaDeApertura;
    private DayOfWeek DiaDeCierre;
    private int HoraApertura;
    private int MinutoApertura;
    private int HoraCierre;
    private int MinutoCierre;
    @BeforeEach
    void setUp() {
        DiaDeApertura = DayOfWeek.MONDAY;
        DiaDeCierre = DayOfWeek.FRIDAY;
        negocioService = new NegocioService(negocioRepository, inventarioRegistroRepository, productoRepository);
        HoraApertura = 9;
        MinutoApertura = 0;
        HoraCierre = 18;
        MinutoApertura = 0;
    }

    @Test
    void sePuedeCrearNegocioNuevo() {
        //when
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
        Optional<Negocio> negocio = negocioRepository.findByNombre("Buffet Paseo Colon");
        //then
        assertThat(negocio.isPresent()).isTrue();
    }

    @Test
    void noSePuedeCrearNegocioExistente() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
        //when
         ResponseEntity<HttpStatus> status = negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
        //then
        assertThat(status).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void seObtienenNegociosExistentes() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
        negocioService.crearNegocio("Buffet Las Heras", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
        negocioService.crearNegocio("Buffet Ciudad Universitaria", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
        //when
        Collection<Negocio> negocios = negocioService.obtenerNegocios();
        //then
        assertThat(negocios.size()).isEqualTo(3);
    }

    @Test
    void sePuedeCrearProductoNuevoEnNegocioExistente() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
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
    void noSePuedeCrearProductoYaExistenteEnNegocioExistente() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20));
        Optional<Negocio> negocio = negocioRepository.findByNombre("Buffet Paseo Colon");
        negocioService.crearProducto(negocio.get().getId(), "Pancho",inventarioRegistroDto);
        //when
        ResponseEntity<HttpStatus> status= negocioService.crearProducto(negocio.get().getId(), "Pancho",inventarioRegistroDto);
        //then
        assertThat(status).isEqualTo(ResponseEntity.internalServerError().build());
    }
    @Test
    void noSePuedeCrearProductoNuevoEnNegocioQueNoExiste() {
        //given
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20));
        //when
        ResponseEntity<HttpStatus> status= negocioService.crearProducto(1L, "Pancho",inventarioRegistroDto);
        //then
        assertThat(status).isEqualTo(ResponseEntity.notFound().build());
    }
    @Test
    void eliminarProducto() {
        //given
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
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
        negocioService.crearNegocio("Buffet Paseo Colon", DiaDeApertura, DiaDeCierre,HoraApertura, MinutoApertura, HoraCierre, MinutoCierre);
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
    @Test
    void noSePuedeobtenerProductosDeUnNegocioQueNoExiste() {
        // when: "Se intenta obtener productos de un negocio que no existe"
        assertThatThrownBy(
                () -> {
                    negocioService.obtenerProductos(1L);
                }
        )
        // then: "se lanza error"
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("No existe el negocio al cual se solicitó obtener sus productos.");
    }

}
