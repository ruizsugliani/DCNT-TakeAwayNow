package com.dcnt.take_away_now.repository;

import com.dcnt.take_away_now.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoPedidoRepository extends JpaRepository<Pedido, Long> {

}
