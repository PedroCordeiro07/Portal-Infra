package org.example.portalinfra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(
    @NotBlank String senhaAtual,
    @NotBlank @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres")
    String novaSenha
) {}