package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.CartaoCredito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, String> {}
