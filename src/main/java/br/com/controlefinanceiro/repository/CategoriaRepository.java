package br.com.controlefinanceiro.repository;
import br.com.controlefinanceiro.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriaRepository extends JpaRepository<Categoria, String> {}
