package org.example.portalinfra.repository;

import org.example.portalinfra.model.Reclamacao;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReclamacaoRepository
        extends JpaRepository<Reclamacao, Long> {

    Page<Reclamacao> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId,  Pageable pageable);

    Page<Reclamacao> findAllByOrderByDataCriacaoDesc(Pageable pageable);
}
