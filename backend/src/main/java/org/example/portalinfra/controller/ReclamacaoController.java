
package org.example.portalinfra.controller;

import jakarta.validation.Valid;

import org.example.portalinfra.dto.ReclamacaoRequest;
import org.example.portalinfra.dto.ReclamacaoResponse;
import org.example.portalinfra.model.Reclamacao;
import org.example.portalinfra.repository.ReclamacaoRepository;
import org.example.portalinfra.service.ReclamacaoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/reclamacoes")
public class ReclamacaoController {

    private final ReclamacaoService reclamacaoService;
    private final ReclamacaoRepository reclamacaoRepository;

    public ReclamacaoController(ReclamacaoService reclamacaoService, ReclamacaoRepository reclamacaoRepository) {
        this.reclamacaoService = reclamacaoService;
        this.reclamacaoRepository = reclamacaoRepository;
    }

    @GetMapping("/contagem")
    public ResponseEntity<java.util.Map<String, Long>> contagem() {
        return ResponseEntity.ok(java.util.Map.of(
            "total", reclamacaoRepository.count(),
            "pendentes", reclamacaoRepository.countByStatusIgnoreCase("Pendente"),
            "resolvidas", reclamacaoRepository.countByStatusIgnoreCase("Resolvida")
        ));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReclamacaoResponse> criar(
            @Valid @ModelAttribute ReclamacaoRequest dados,
            @RequestPart("foto") MultipartFile foto,
            @RequestParam("usuarioId") Long usuarioId) {

        ReclamacaoResponse resposta =
            reclamacaoService.criar(dados, foto, usuarioId);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(resposta);
    }

    @GetMapping
    public ResponseEntity<Page<ReclamacaoResponse>> listarTodas(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reclamacaoService.listarTodas(pageable));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<ReclamacaoResponse>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(reclamacaoService.listarPorUsuario(usuarioId, pageable));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPeloUsuario(
            @PathVariable Long id,
            @RequestParam("usuarioId") Long usuarioId) {
        reclamacaoService.deletarPeloUsuario(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deletarPeloAdmin(
            @PathVariable Long id,
            @RequestParam("adminId") Long adminId) {
        reclamacaoService.deletarPeloAdmin(id, adminId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<ReclamacaoResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam("adminId") Long adminId,
            @RequestBody java.util.Map<String, String> body) {

        ReclamacaoResponse atualizada = reclamacaoService.atualizarStatus(id, body.get("status"), adminId);
        return ResponseEntity.ok(atualizada);
    }


    @PatchMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<org.example.portalinfra.dto.ReclamacaoResponse> editarReclamacao(
            @PathVariable Long id,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestPart("dados") org.example.portalinfra.dto.ReclamacaoRequest request,
            @RequestPart(value = "foto", required = false) org.springframework.web.multipart.MultipartFile novaFoto) {

        var response = reclamacaoService.editarReclamacao(id, usuarioId, request, novaFoto);
        return org.springframework.http.ResponseEntity.ok(response);
    }

}
