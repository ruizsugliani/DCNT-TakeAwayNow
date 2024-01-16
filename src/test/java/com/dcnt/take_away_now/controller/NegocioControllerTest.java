package com.dcnt.take_away_now.controller;

import com.dcnt.take_away_now.domain.Negocio;
import com.dcnt.take_away_now.repository.NegocioRepository;
import com.dcnt.take_away_now.service.NegocioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers =  NegocioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class NegocioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NegocioService negocioService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NegocioRepository negocioRepository;
    private DayOfWeek DiaDeApertura;
    private DayOfWeek DiaDeCierre;
    private int HoraApertura;
    private int MinutoApertura;
    private int HoraCierre;
    private int MinutoCierre;
    String jsonString;
    @BeforeEach
    void setUp() {
        DiaDeApertura = DayOfWeek.MONDAY;
        DiaDeCierre = DayOfWeek.FRIDAY;
        HoraApertura = 9;
        MinutoApertura = 0;
        HoraCierre = 18;
        MinutoCierre = 0;
        jsonString = "{ \"nombre\" : \"Paseo Colon\", \"diaDeApertura\" : " + DiaDeApertura + ", \"diaDeCierre\" : " + DiaDeCierre +
                ", \"horaApertura\" : " + HoraApertura + ", \"minutoApertura\" : " + MinutoApertura +
                ", \"horaCierre\" : " + HoraCierre + ", \"minutoCierre\" : " + MinutoCierre + " }";
    }

    @Test
    void sePuedeCrearNegocio() throws  Exception {
        ResultActions response = mockMvc.perform(post("/api/negocios/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombre", "Paseo Colon")
                .param("diaDeApertura", String.valueOf(DiaDeApertura))
                .param("diaDeCierre", String.valueOf(DiaDeCierre))
                .param("horaApertura", String.valueOf(HoraApertura))
                .param("minutoApertura", String.valueOf(MinutoApertura))
                .param("horaCierre", String.valueOf(HoraCierre))
                .param("minutoCierre", String.valueOf(MinutoCierre))
        );
        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void noSePuedeCrearNegocioSiFaltanDatos() throws  Exception {
        ResultActions response = mockMvc.perform(post("/api/negocios/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("diaDeApertura", String.valueOf(DiaDeApertura))
                .param("diaDeCierre", String.valueOf(DiaDeCierre))
                .param("horaApertura", String.valueOf(HoraApertura))
                .param("minutoApertura", String.valueOf(MinutoApertura))
                .param("horaCierre", String.valueOf(HoraCierre))
                .param("minutoCierre", String.valueOf(MinutoCierre))
        );
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void sePuedeCrearProductoNuevo() throws Exception {
        //given
        mockMvc.perform(post("/api/negocios/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombre", "Paseo Colon")
                .param("diaDeApertura", String.valueOf(DiaDeApertura))
                .param("diaDeCierre", String.valueOf(DiaDeCierre))
                .param("horaApertura", String.valueOf(HoraApertura))
                .param("minutoApertura", String.valueOf(MinutoApertura))
                .param("horaCierre", String.valueOf(HoraCierre))
                .param("minutoCierre", String.valueOf(MinutoCierre))
        );

        ResultActions response = mockMvc.perform(post("/api/negocios/" + 1 + "/productos/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nombreDelProducto", "Pancho")
                        .param("stock", "5")
                        .param("precio", "100")
                        .param("recompensaPuntosDeConfianza", "20")
        );

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /* TODO VERIFICAR PORQUE EL SEGUNDO POST TIENE STATUS 200 CUANDO TIENE QUE SER 500
    @Test
    void noSePuedeCrearProductoQueYaExisteEnEseNegocio() throws Exception {
        //given
        mockMvc.perform(post("/api/negocios/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombre", "Paseo Colon")
                .param("diaDeApertura", String.valueOf(DiaDeApertura))
                .param("diaDeCierre", String.valueOf(DiaDeCierre))
                .param("horaApertura", String.valueOf(HoraApertura))
                .param("minutoApertura", String.valueOf(MinutoApertura))
                .param("horaCierre", String.valueOf(HoraCierre))
                .param("minutoCierre", String.valueOf(MinutoCierre))
        );

        mockMvc.perform(post("/api/negocios/" + 1 + "/productos/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombreDelProducto", "Pancho")
                .param("stock", "5")
                .param("precio", "100")
                .param("recompensaPuntosDeConfianza", "20")
        );

        ResultActions response = mockMvc.perform(post("/api/negocios/" + 1 + "/productos/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombreDelProducto", "Pancho")
                .param("stock", "5")
                .param("precio", "100")
                .param("recompensaPuntosDeConfianza", "20")
        );

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }*/

    @Test
    void obtenerProductos() throws Exception {
        //given
        mockMvc.perform(post("/api/negocios/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombre", "Paseo Colon")
                .param("diaDeApertura", String.valueOf(DiaDeApertura))
                .param("diaDeCierre", String.valueOf(DiaDeCierre))
                .param("horaApertura", String.valueOf(HoraApertura))
                .param("minutoApertura", String.valueOf(MinutoApertura))
                .param("horaCierre", String.valueOf(HoraCierre))
                .param("minutoCierre", String.valueOf(MinutoCierre))
        );

        mockMvc.perform(post("/api/negocios/" + 1 + "/productos/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombreDelProducto", "Pancho")
                .param("stock", "5")
                .param("precio", "100")
                .param("recompensaPuntosDeConfianza", "20")
        );
        mockMvc.perform(post("/api/negocios/" + 1 + "/productos/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombreDelProducto", "Coca Cola")
                .param("stock", "5")
                .param("precio", "100")
                .param("recompensaPuntosDeConfianza", "20")
        );
        mockMvc.perform(post("/api/negocios/" + 1 + "/productos/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nombreDelProducto", "Alfajor")
                .param("stock", "5")
                .param("precio", "100")
                .param("recompensaPuntosDeConfianza", "20")
        );
        ResultActions response = mockMvc.perform(get("/api/negocios/"+ 1 + "/productos")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void obtenerNegocios() throws  Exception {
        ResultActions response = mockMvc.perform(get("/api/negocios/")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void modificarProducto() {
    }

    @Test
    void modificarHorariosDelNegocio() {
    }

    @Test
    void modificarDiasDeAperturaDelNegocio() {
    }

    @Test
    void eliminarProducto() {
    }
}