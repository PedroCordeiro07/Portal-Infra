package org.example.portalinfra.dto;

import org.example.portalinfra.model.Comentario;
import java.time.LocalDateTime;

public record ComentarioResponse(
    Long id,
    String texto,
    LocalDateTime dataCriacao,
    Long usuarioId,
    Long reclamacaoId
) {
    public static ComentarioResponse de(Comentario c) {
        return new ComentarioResponse(
            c.getId(),
            c.getTexto(),
            c.getDataCriacao(),
            c.getUsuario().getId(),
            c.getReclamacao().getId()
        );
    }
}
