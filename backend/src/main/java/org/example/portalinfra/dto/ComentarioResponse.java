package org.example.portalinfra.dto;

import org.example.portalinfra.model.Comentario;
import java.time.LocalDateTime;
import java.util.List;

public record ComentarioResponse(
        Long id,
        String texto,
        String fotoUrl,
        boolean isResolucao,
        LocalDateTime dataCriacao,
        Long usuarioId,
        String usuarioNome,
        Long reclamacaoId,
        Long comentarioPaiId,
        List<ComentarioResponse> respostas
) {
    public static ComentarioResponse de(Comentario c) {
        List<ComentarioResponse> listaRespostas = c.getRespostas() != null
                ? c.getRespostas().stream().map(ComentarioResponse::de).toList()
                : List.of();

        return new ComentarioResponse(
                c.getId(),
                c.getTexto(),
                c.getFotoUrl(),
                c.isResolucao(),
                c.getDataCriacao(),
                c.getUsuario().getId(),
                c.getUsuario().getNome(),
                c.getReclamacao().getId(),
                c.getComentarioPai() != null ? c.getComentarioPai().getId() : null,
                listaRespostas
        );
    }
}