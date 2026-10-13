package org.example.portalinfra.dto;

public record DenunciaRequest(
        Long reclamacaoId,   // Envia este
        Long comentarioId,   // OU este (nunca os dois)
        String tipo,
        String descricao
) {}