package com.dcnt.take_away_now.service;

import com.dcnt.take_away_now.domain.*;
import com.dcnt.take_away_now.dto.InfoPedidoDto;
import com.dcnt.take_away_now.dto.InventarioRegistroDto;
import com.dcnt.take_away_now.repository.*;
import com.dcnt.take_away_now.value_object.Dinero;
import com.dcnt.take_away_now.value_object.PuntosDeConfianza;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class PedidoServiceTest {
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private NegocioRepository negocioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private InventarioRegistroRepository inventarioRegistroRepository;
    @Autowired
    private ProductoPedidoRepository productoPedidoRepository;
    private NegocioService negocioService;

    private PedidoService pedidoService;

    Cliente cliente;
    Negocio negocio;
    Producto pancho;
    @BeforeEach
    void setUp() {
        cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        negocio = new Negocio("Paseo Colon", LocalTime.of(14, 0),LocalTime.of(21, 0), DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        negocioRepository.save(negocio);
        pancho = new Producto("Pancho");
        productoRepository.save(pancho);
        negocioService = new NegocioService(negocioRepository, inventarioRegistroRepository, productoRepository,pedidoRepository);
        pedidoService = new PedidoService(pedidoRepository, clienteRepository, negocioRepository,productoRepository, inventarioRegistroRepository,productoPedidoRepository);
    }

    @Test
    void obtenerPedidos() {
        //given
        Pedido pedido1 = new Pedido(negocio, cliente );
        pedidoRepository.save(pedido1);
        Pedido pedido2 = new Pedido(negocio, cliente );
        pedidoRepository.save(pedido2);
        Pedido pedido3 = new Pedido(negocio, cliente );
        pedidoRepository.save(pedido3);

        //when
        Collection<Pedido> pedidos = pedidoService.obtenerPedidos();

        //then
        assertThat(pedidos.size()).isEqualTo(3);
    }
    @Test
    void esUnProductoDeEseNegocio() {
        //given
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20.0));
        negocioService.crearProducto(negocio.getId(), "Alfajor",inventarioRegistroDto);
        Optional<Producto> alfajor = productoRepository.findByNombre("Alfajor");

        //when
        boolean esProductoDeEseNegocio = pedidoService.esUnProductoDeEseNegocio(negocio.getId(), alfajor.get().getId());

        //then
        assertThat(esProductoDeEseNegocio).isTrue();
    }
    @Test
    void noEsUnProductoDeEseNegocio() {
        //when
        boolean noEsProductoDeEseNegocio = pedidoService.esUnProductoDeEseNegocio(negocio.getId(), pancho.getId());

        //then
        assertThat(noEsProductoDeEseNegocio).isFalse();
    }
    @Test
    void sePuedeConfirmarUnPedidoParaEstosProductos() {
        //given
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20.0));
        negocioService.crearProducto(negocio.getId(), "Alfajor",inventarioRegistroDto);
        negocioService.crearProducto(negocio.getId(), "Coca Cola",inventarioRegistroDto);
        negocioService.crearProducto(negocio.getId(), "Paraguitas",inventarioRegistroDto);
        Optional<Producto> alfajor = productoRepository.findByNombre("Alfajor");
        Optional<Producto> coca = productoRepository.findByNombre("Coca Cola");
        Optional<Producto> paraguitas = productoRepository.findByNombre("Paraguitas");
        Map<Long, Integer> productos =Map.of(alfajor.get().getId(), 2, coca.get().getId(), 3, paraguitas.get().getId(), 1);

        //when
        boolean sePuede = pedidoService.sePuedeConfirmarUnPedidoParaEstosProductos(productos, negocio.getId());

        //then
        assertThat(sePuede).isTrue();
    }

    @Test
    void noSePuedeConfirmarUnPedidoParaEstosProductosPorqueNoEsUnProductoDelNegocio() {
        //given
        Map<Long, Integer> productos =Map.of(pancho.getId(), 1);

        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.sePuedeConfirmarUnPedidoParaEstosProductos(productos, negocio.getId());
                }
        )
        // then: "se lanza error"
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Ha ocurrido un error ya que el producto " + pancho.getNombre() + " no está disponible para este negocio.");
    }
    @Test
    void noSePuedeConfirmarUnPedidoParaEstosProductosPorqueNoHayStockSuficiente() {
        //given
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20.0));
        negocioService.crearProducto(negocio.getId(), "Alfajor",inventarioRegistroDto);
        Optional<Producto> alfajor = productoRepository.findByNombre("Alfajor");

        Map<Long, Integer> productos =Map.of(alfajor.get().getId(), 11);

        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.sePuedeConfirmarUnPedidoParaEstosProductos(productos, negocio.getId());
                }
        )
                // then: "se lanza error"
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("La cantidad solicitada para el producto " + alfajor.get().getNombre() + " es mayor al stock disponible.");
    }

    @Test
    void sePuedeConfirmarPedido() {
        //given
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20.0));
        negocioService.crearProducto(negocio.getId(), "Alfajor",inventarioRegistroDto);
        Optional<Producto> alfajor = productoRepository.findByNombre("Alfajor");

        Map<Long, Integer> productos =Map.of(alfajor.get().getId(), 9);
        InfoPedidoDto infoPedidoDto = new InfoPedidoDto(cliente.getId(), negocio.getId(), productos);

        //when
        ResponseEntity<HttpStatus> status = pedidoService.confirmarPedido(infoPedidoDto);


        assertThat(status).isEqualTo(ResponseEntity.ok().build());
    }

    @Test
    void verificarPedido() {
        //given
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20.0));
        negocioService.crearProducto(negocio.getId(), "Alfajor",inventarioRegistroDto);
        Optional<Producto> alfajor = productoRepository.findByNombre("Alfajor");

        Map<Long, Integer> productos =Map.of(alfajor.get().getId(), 10);
        InfoPedidoDto infoPedidoDto = new InfoPedidoDto(cliente.getId(), negocio.getId(), productos);

        //when
        ResponseEntity<HttpStatus> status = pedidoService.verificarPedido(infoPedidoDto);

        //then
        assertThat(status).isEqualTo(ResponseEntity.ok().build());
    }

    @Test
    void verificarPedidoLanzaQueNoExisteCliente() {
        //given
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20.0));
        negocioService.crearProducto(negocio.getId(), "Alfajor",inventarioRegistroDto);
        Optional<Producto> alfajor = productoRepository.findByNombre("Alfajor");

        Map<Long, Integer> productos =Map.of(alfajor.get().getId(), 10);
        InfoPedidoDto infoPedidoDto = new InfoPedidoDto(33L, negocio.getId(), productos);

        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.verificarPedido(infoPedidoDto);
                }
        )
        // then: "se lanza error"
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    void verificarPedidoLanzaQueNoExisteNegocio() {
        //given
        InventarioRegistroDto inventarioRegistroDto = new InventarioRegistroDto(10L, new Dinero(100), new PuntosDeConfianza(20.0));
        negocioService.crearProducto(negocio.getId(), "Alfajor",inventarioRegistroDto);
        Optional<Producto> alfajor = productoRepository.findByNombre("Alfajor");

        Map<Long, Integer> productos =Map.of(alfajor.get().getId(), 10);
        InfoPedidoDto infoPedidoDto = new InfoPedidoDto(cliente.getId(), 33L, productos);

        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.verificarPedido(infoPedidoDto);
                }
        )
        // then: "se lanza error"
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Negocio no encontrado");
    }

    @Test
    void sePuedeMarcarComienzoDePreparacionAUnPedidoQueEstaAguardandoPreparacion() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);

        //when
        ResponseEntity<HttpStatus> status = pedidoService.marcarComienzoDePreparacion(pedido1.getId());

        //then
        assertThat(status).isEqualTo(ResponseEntity.accepted().build());
    }

    @Test
    void noSePuedeMarcarComienzoDePreparacionAUnPedidoQueNoExiste() {
        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.marcarComienzoDePreparacion(1L);
                }
        )
        // then: "se lanza error"
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("No existe el pedido al cual usted quiere marcar su comienzo de preparación.");
    }

    @Test
    void noSePuedeMarcarComienzoDePreparacionAUnPedidoQueNoEstaEnAguardandoPreparacion() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);
        pedidoService.marcarComienzoDePreparacion(pedido1.getId());

        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.marcarComienzoDePreparacion(pedido1.getId());
                }
        )
        // then: "se lanza error"
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("No se puede comenzar a preparar dicho pedido ya que el mismo no se encuentra aguardando preparación.");
    }

    @Test
    void sePuedeMarcarListoParaRetirarAUnPedidoQueEstaEnPreparacion() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);
        pedidoService.marcarComienzoDePreparacion(pedido1.getId());

        //when
        ResponseEntity<HttpStatus> status = pedidoService.marcarPedidoListoParaRetirar(pedido1.getId());

        //then
        assertThat(status).isEqualTo(ResponseEntity.accepted().build());
    }
    @Test
    void noSePuedeMarcarListoParaRetirarAUnPedidoQueNoExiste() {
        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.marcarPedidoListoParaRetirar(1L);
                }
        )
        // then: "se lanza error"
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("No existe el pedido al cual usted quiere marcar como disponible su retiro.");
    }

    @Test
    void noSePuedeMarcarListoParaRetirarAUnPedidoQueNoEstaEnPreparacion() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);

        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.marcarPedidoListoParaRetirar(pedido1.getId());
                }
        )
        // then: "se lanza error"
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("No se puede marcar dicho pedido como lista para retirar ya que el mismo no se encuentra en preparación.");
    }

    @Test
    void sePuedeConfirmarRetiroAUnPedidoQueEstaListoParaRetirar() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);
        pedidoService.marcarComienzoDePreparacion(pedido1.getId());
        pedidoService.marcarPedidoListoParaRetirar(pedido1.getId());

        //when
        ResponseEntity<HttpStatus> status = pedidoService.confirmarRetiroDelPedido(pedido1.getId());

        //then
        assertThat(status).isEqualTo(ResponseEntity.accepted().build());
    }

    @Test
    void noSePuedeConfirmarRetiroAUnPedidoQueNoExiste() {
        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.confirmarRetiroDelPedido(1L);
                }
        )
        // then: "se lanza error"
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("No existe el pedido al cual usted quiere confirmar el retiro.");
    }

    @Test
    void noSePuedeConfirmarRetiroAUnPedidoQueNoEstaListoParaRetirar() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);

        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.confirmarRetiroDelPedido(pedido1.getId());
                }
        )
        // then: "se lanza error"
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("No se puede retirar dicho pedido ya que el mismo no se encuentra listo para retirar.");
    }

    @Test
    void sePuedeCancelarAUnPedidoQueEstaAguardandoPreparacion() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);

        //when
        ResponseEntity<HttpStatus> status = pedidoService.cancelarPedido(pedido1.getId());

        //then
        assertThat(status).isEqualTo(ResponseEntity.accepted().build());
    }

    @Test
    void noSePuedeCancelarAUnPedidoQueNoExiste() {
        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.cancelarPedido(1L);
                }
        )
                // then: "se lanza error"
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe el pedido que usted busca cancelar.");
    }

    @Test
    void noSePuedeCancelarAUnPedidoQueEstaRetirado() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);
        pedidoService.marcarComienzoDePreparacion(pedido1.getId());
        pedidoService.marcarPedidoListoParaRetirar(pedido1.getId());
        pedidoService.confirmarRetiroDelPedido(pedido1.getId());
        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.cancelarPedido(pedido1.getId());
                }
        )
                // then: "se lanza error"
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se puede retirar dicho pedido ya que el mismo no se encuentra aguardando preparación, en preparación ni listo para retirar.");
    }

    @Test
    void sePuedeMarcarPedidoDevueltoAUnPedidoQueEstaRetirado() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);
        pedidoService.marcarComienzoDePreparacion(pedido1.getId());
        pedidoService.marcarPedidoListoParaRetirar(pedido1.getId());
        pedidoService.confirmarRetiroDelPedido(pedido1.getId());

        //when
        ResponseEntity<HttpStatus> status = pedidoService.devolverPedido(pedido1.getId());

        //then
        assertThat(status).isEqualTo(ResponseEntity.accepted().build());
    }

    @Test
    void noSePuedeMarcarPedidoDevueltoAUnPedidoQueNoExiste() {
        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.devolverPedido(1L);
                }
        )
                // then: "se lanza error"
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No existe el pedido que usted busca devolver.");
    }

    @Test
    void noSePuedeMarcarPedidoDevueltoAUnPedidoQueNoEstaRetirado() {
        //given
        Cliente cliente = new Cliente("Messi");
        clienteRepository.save(cliente);
        Pedido pedido1 = new Pedido(negocio, cliente);
        pedidoRepository.save(pedido1);

        //when
        assertThatThrownBy(
                () -> {
                    pedidoService.devolverPedido(pedido1.getId());
                }
        )
                // then: "se lanza error"
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se puede devolver dicho pedido ya que el mismo no se encontraba retirado.");
    }
}