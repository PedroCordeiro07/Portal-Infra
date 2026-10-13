package org.example.portalinfra.repository;

import org.example.portalinfra.model.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    // Fila de moderação: Apenas Reclamações
    List<Denuncia> findByReclamacaoIsNotNullAndStatusOrderByDataCriacaoAsc(String status);

    // Fila de moderação: Apenas Comentários
    List<Denuncia> findByComentarioIsNotNullAndStatusOrderByDataCriacaoAsc(String status);

    // Regra anti-spam: Verifica se o usuário já denunciou a mesma reclamação
    boolean existsByUsuarioIdAndReclamacaoId(Long usuarioId, Long reclamacaoId);

    // Regra anti-spam: Verifica se o usuário já denunciou o mesmo comentário
    boolean existsByUsuarioIdAndComentarioId(Long usuarioId, Long comentarioId);
}