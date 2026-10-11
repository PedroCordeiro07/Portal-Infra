package org.example.portalinfra.dto;

import jakarta.validation.constraints.NotBlank;

public record ComentarioRequest(
    @NotBlank
    String texto
) {}
