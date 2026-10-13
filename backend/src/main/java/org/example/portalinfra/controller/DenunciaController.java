package org.example.portalinfra.controller;

import org.example.portalinfra.dto.DenunciaRequest;
import org.example.portalinfra.dto.DenunciaResponse;
import org.example.portalinfra.service.DenunciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam("usuarioId") Long usuarioId,
            @RequestBody DenunciaRequest request) {

        DenunciaResponse response = denunciaService.criarDenuncia(usuarioId, request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping("/reclamacoes")
    public ResponseEntity<List<DenunciaResponse>> listarDenunciasDeReclamacoes(@RequestParam("adminId") Long adminId) {
        return ResponseEntity.ok(denunciaService.listarDenunciasDeReclamacoes(adminId));
    }

    @GetMapping("/comentarios")
    public ResponseEntity<List<DenunciaResponse>> listarDenunciasDeComentarios(@RequestParam("adminId") Long adminId) {
        return ResponseEntity.ok(denunciaService.listarDenunciasDeComentarios(adminId));
    }


    @PatchMapping("/{id}/resolver")
    public ResponseEntity<DenunciaResponse> resolverDenuncia(
            @PathVariable Long id,
            @RequestParam("adminId") Long adminId,
            @RequestParam("aceitar") boolean aceitar) {

        DenunciaResponse response = denunciaService.resolverDenuncia(id, adminId, aceitar);
        return ResponseEntity.ok(response);
    }
}