package com.dcnt.take_away_now.repository;

import com.dcnt.take_away_now.domain.InventarioRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRegistroRepository extends JpaRepository<InventarioRegistro, Long> {

}
