package org.example.portalinfra.service;

import org.example.portalinfra.dto.ComentarioRequest;
import org.example.portalinfra.dto.ComentarioResponse;
import org.example.portalinfra.model.Comentario;
import org.example.portalinfra.model.Reclamacao;
import org.example.portalinfra.model.Usuario;
import org.example.portalinfra.repository.ComentarioRepository;
import org.example.portalinfra.repository.ReclamacaoRepository;
import org.example.portalinfra.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReclamacaoRepository reclamacaoRepository;

    public ComentarioService(
        ComentarioRepository comentarioRepository,
        UsuarioRepository usuarioRepository,
        ReclamacaoRepository reclamacaoRepository
    ) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.reclamacaoRepository = reclamacaoRepository;
    }

    @Transactional
    public ComentarioResponse criar(Long usuarioId, Long reclamacaoId, ComentarioRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        Reclamacao reclamacao = reclamacaoRepository.findById(reclamacaoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reclamação não encontrada."));

        if (!usuario.isEstaAtivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta de usuário está desativada.");
        }

        Comentario comentario = new Comentario();
        comentario.setTexto(request.texto());
        comentario.setUsuario(usuario);
        comentario.setReclamacao(reclamacao);

        Comentario salvo = comentarioRepository.save(comentario);
        return ComentarioResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponse> listarPorReclamacao(Long reclamacaoId) {
        return comentarioRepository.findByReclamacaoIdOrderByDataCriacaoAsc(reclamacaoId)
            .stream()
            .map(ComentarioResponse::de)
            .toList();
    }
}
