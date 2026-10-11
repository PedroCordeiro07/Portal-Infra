package org.example.portalinfra.controller;

import org.example.portalinfra.dto.ComentarioRequest;
import org.example.portalinfra.dto.ComentarioResponse;
import org.example.portalinfra.service.ComentarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comentarios")
@Validated
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @PostMapping
    public ResponseEntity<ComentarioResponse> criar(
        @RequestParam Long usuarioId,
        @RequestParam Long reclamacaoId,
        @RequestBody @Validated ComentarioRequest request
    ) {
        ComentarioResponse resposta = comentarioService.criar(usuarioId, reclamacaoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/reclamacao/{reclamacaoId}")
    public ResponseEntity<List<ComentarioResponse>> listarPorReclamacao(@PathVariable Long reclamacaoId) {
        return ResponseEntity.ok(comentarioService.listarPorReclamacao(reclamacaoId));
    }
}
