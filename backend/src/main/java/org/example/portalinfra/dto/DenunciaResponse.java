package org.example.portalinfra.dto;

import org.example.portalinfra.model.Denuncia;
import java.time.LocalDateTime;

public record DenunciaResponse(
        Long id,
        Long usuarioId,
        Long reclamacaoId,
        Long comentarioId,
        String tipo,
        String descricao,
        String status,
        LocalDateTime dataCriacao
) {
    public static DenunciaResponse de(Denuncia denuncia) {
        return new DenunciaResponse(
                denuncia.getId(),
                denuncia.getUsuario().getId(),
                denuncia.getReclamacao() != null ? denuncia.getReclamacao().getId() : null,
                denuncia.getComentario() != null ? denuncia.getComentario().getId() : null,
                denuncia.getTipo(),
                denuncia.getDescricao(),
                denuncia.getStatus(),
                denuncia.getDataCriacao()
        );
    }
}