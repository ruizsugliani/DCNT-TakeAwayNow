package com.dcnt.take_away_now.repository;

import com.dcnt.take_away_now.domain.InventarioRegistro;
import com.dcnt.take_away_now.domain.Negocio;
import com.dcnt.take_away_now.domain.Producto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRegistroRepository extends JpaRepository<InventarioRegistro, Long> {
    @Transactional
    void deleteByNegocioAndProducto(Negocio negocio, Producto producto);

    @Transactional
    Optional<InventarioRegistro> findByNegocioAndProducto(Negocio negocio, Producto producto);

    boolean existsByNegocioAndProducto(Negocio negocio, Producto producto);
}
