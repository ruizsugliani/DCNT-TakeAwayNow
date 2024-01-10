package com.dcnt.take_away_now.controller;

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
    void crearNegocio() throws  Exception {
        ResultActions response = mockMvc.perform(post("/api/")
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
    void crearProducto() {
    }

    @Test
    void obtenerProductos() {
    }

    @Test
    void obtenerNegocios() throws  Exception {
        ResultActions response = mockMvc.perform(get("/api/")
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