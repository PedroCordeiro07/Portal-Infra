package org.example.portalinfra.controller;

import org.example.portalinfra.dto.DenunciaRequest;
import org.example.portalinfra.dto.DenunciaResponse;
import org.example.portalinfra.service.DenunciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.portalinfra.config.SessaoUtil;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/denuncias")
public class DenunciaController {

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }


    @PostMapping
    public ResponseEntity<DenunciaResponse> criarDenuncia(
            Authentication auth,
            @RequestBody DenunciaRequest request) {
        return ResponseEntity.status(201).body(
            denunciaService.criarDenuncia(SessaoUtil.idLogado(auth), request));
    }

    @GetMapping("/reclamacoes")
    public ResponseEntity<List<DenunciaResponse>> listarDenunciasDeReclamacoes(Authentication auth) {
        return ResponseEntity.ok(
            denunciaService.listarDenunciasDeReclamacoes(SessaoUtil.idLogado(auth)));
    }
    @GetMapping("/comentarios")
    public ResponseEntity<List<DenunciaResponse>> listarDenunciasDeComentarios(Authentication auth) {
        return ResponseEntity.ok(
            denunciaService.listarDenunciasDeComentarios(SessaoUtil.idLogado(auth)));
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<DenunciaResponse> resolverDenuncia(
            @PathVariable Long id,
            Authentication auth,
            @RequestParam("aceitar") boolean aceitar) {
        return ResponseEntity.ok(
            denunciaService.resolverDenuncia(id, SessaoUtil.idLogado(auth), aceitar));
    }
}