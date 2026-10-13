package org.example.portalinfra.controller;

import jakarta.validation.Valid;
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

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComentarioResponse> criar(
            @RequestParam Long usuarioId,
            @RequestParam Long reclamacaoId,
            @Valid @ModelAttribute ComentarioRequest request,
            @RequestPart(value = "foto", required = false) org.springframework.web.multipart.MultipartFile foto
    ) {
        ComentarioResponse resposta = comentarioService.criar(usuarioId, reclamacaoId, request, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/reclamacao/{reclamacaoId}")
    public ResponseEntity<List<ComentarioResponse>> listarPorReclamacao(@PathVariable Long reclamacaoId) {
        return ResponseEntity.ok(comentarioService.listarPorReclamacao(reclamacaoId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPeloUsuario(
            @PathVariable Long id,
            @RequestParam("usuarioId") Long usuarioId) {
        comentarioService.deletarPeloUsuario(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deletarPeloAdmin(
            @PathVariable Long id,
            @RequestParam("adminId") Long adminId) {
        comentarioService.deletarComentarioPeloAdmin(id, adminId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<org.example.portalinfra.dto.ComentarioResponse> editarComentario(
            @PathVariable Long id,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("texto") String novoTexto,
            @RequestParam(value = "isResolucao", required = false) Boolean isResolucao,
            @RequestParam(value = "removerFoto", defaultValue = "false") boolean removerFoto,
            @RequestPart(value = "foto", required = false) org.springframework.web.multipart.MultipartFile novaFoto) {

        var response = comentarioService.editarComentario(id, usuarioId, novoTexto, isResolucao, novaFoto, removerFoto);
        return org.springframework.http.ResponseEntity.ok(response);
    }


}
