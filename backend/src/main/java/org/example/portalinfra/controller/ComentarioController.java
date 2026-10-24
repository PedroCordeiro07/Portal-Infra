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
import org.example.portalinfra.config.SessaoUtil;
import org.springframework.security.core.Authentication;

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
            Authentication auth,
            @RequestParam Long reclamacaoId,
            @Valid @ModelAttribute ComentarioRequest request,
            @RequestPart(value = "foto", required = false) org.springframework.web.multipart.MultipartFile foto) {

        ComentarioResponse resposta = comentarioService.criar(
            SessaoUtil.idLogado(auth), reclamacaoId, request, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/reclamacao/{reclamacaoId}")
    public ResponseEntity<List<ComentarioResponse>> listarPorReclamacao(@PathVariable Long reclamacaoId) {
        return ResponseEntity.ok(comentarioService.listarPorReclamacao(reclamacaoId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPeloUsuario(
            @PathVariable Long id, Authentication auth) {
        comentarioService.deletarPeloUsuario(id, SessaoUtil.idLogado(auth));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deletarPeloAdmin(
            @PathVariable Long id, Authentication auth) {
        comentarioService.deletarComentarioPeloAdmin(id, SessaoUtil.idLogado(auth));
        return ResponseEntity.noContent().build();
    }



    @PatchMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComentarioResponse> editarComentario(
            @PathVariable Long id,
            Authentication auth,
            @RequestParam("texto") String novoTexto,
            @RequestParam(value = "isResolucao", required = false) Boolean isResolucao,
            @RequestParam(value = "removerFoto", defaultValue = "false") boolean removerFoto,
            @RequestPart(value = "foto", required = false) org.springframework.web.multipart.MultipartFile novaFoto) {

        return ResponseEntity.ok(comentarioService.editarComentario(
            id, SessaoUtil.idLogado(auth), novoTexto, isResolucao, novaFoto, removerFoto));
    }


}
