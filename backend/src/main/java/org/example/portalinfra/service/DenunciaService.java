package org.example.portalinfra.service;

import org.example.portalinfra.dto.DenunciaRequest;
import org.example.portalinfra.dto.DenunciaResponse;
import org.example.portalinfra.model.Comentario;
import org.example.portalinfra.model.Denuncia;
import org.example.portalinfra.model.Reclamacao;
import org.example.portalinfra.model.Usuario;
import org.example.portalinfra.repository.ComentarioRepository;
import org.example.portalinfra.repository.DenunciaRepository;
import org.example.portalinfra.repository.ReclamacaoRepository;
import org.example.portalinfra.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class DenunciaService {

    private final DenunciaRepository denunciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReclamacaoRepository reclamacaoRepository;
    private final ComentarioRepository comentarioRepository;
    private final ReclamacaoService reclamacaoService;
    private final ComentarioService comentarioService;

    // A lista das categorias de denuncia permitidas
    private static final List<String> TIPOS_VALIDOS = List.of(
            "CONTEUDO_SEXUAL",
            "DISCURSO_DE_ODIO",
            "PALAVRAS_DE_BAIXO_CALAO",
            "CONTEUDO_FALSO",
            "SPAM",
            "VENDA_OU_SERVICOS",
            "OUTROS"
    );

    public DenunciaService(DenunciaRepository denunciaRepository,
                           UsuarioRepository usuarioRepository,
                           ReclamacaoRepository reclamacaoRepository,
                           ComentarioRepository comentarioRepository,
                           ReclamacaoService reclamacaoService,
                           ComentarioService comentarioService) {
        this.denunciaRepository = denunciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.reclamacaoRepository = reclamacaoRepository;
        this.comentarioRepository = comentarioRepository;
        this.reclamacaoService = reclamacaoService;
        this.comentarioService = comentarioService;
    }

    @Transactional
    public DenunciaResponse criarDenuncia(Long usuarioId, DenunciaRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        if (!TIPOS_VALIDOS.contains(request.tipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de denúncia inválido.");
        }

        boolean temReclamacao = request.reclamacaoId() != null;
        boolean temComentario = request.comentarioId() != null;

        if (temReclamacao && temComentario) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uma denúncia não pode apontar para uma reclamação e um comentário ao mesmo tempo.");
        }
        if (!temReclamacao && !temComentario) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A denúncia deve apontar para uma reclamação ou um comentário.");
        }

        Denuncia denuncia = new Denuncia();
        denuncia.setUsuario(usuario);
        denuncia.setTipo(request.tipo());
        denuncia.setDescricao(request.descricao());

        if (temReclamacao) {
            Reclamacao reclamacao = reclamacaoRepository.findById(request.reclamacaoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reclamação não encontrada."));

            if (reclamacao.getUsuario().getId().equals(usuarioId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode denunciar sua própria reclamação.");
            }
            if (denunciaRepository.existsByUsuarioIdAndReclamacaoId(usuarioId, request.reclamacaoId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já denunciou esta reclamação.");
            }
            denuncia.setReclamacao(reclamacao);
        } else {
            Comentario comentario = comentarioRepository.findById(request.comentarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado."));

            if (comentario.getUsuario().getId().equals(usuarioId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode denunciar seu próprio comentário.");
            }
            if (denunciaRepository.existsByUsuarioIdAndComentarioId(usuarioId, request.comentarioId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já denunciou este comentário.");
            }
            denuncia.setComentario(comentario);
        }

        Denuncia salva = denunciaRepository.save(denuncia);
        return DenunciaResponse.de(salva);
    }


    // ==========================================
    // LEITURAS PARA O ADMIN (FILAS SEPARADAS)
    // ==========================================

    public List<DenunciaResponse> listarDenunciasDeReclamacoes(Long adminId) {
        verificarAdmin(adminId);
        return denunciaRepository.findByReclamacaoIsNotNullAndStatusOrderByDataCriacaoAsc("PENDENTE")
                .stream().map(DenunciaResponse::de).toList();
    }

    public List<DenunciaResponse> listarDenunciasDeComentarios(Long adminId) {
        verificarAdmin(adminId);
        return denunciaRepository.findByComentarioIsNotNullAndStatusOrderByDataCriacaoAsc("PENDENTE")
                .stream().map(DenunciaResponse::de).toList();
    }

    // ==========================================
    // AÇÃO DE MODERAÇÃO
    // ==========================================

    @Transactional
    public DenunciaResponse resolverDenuncia(Long denunciaId, Long adminId, boolean aceitar) {
        verificarAdmin(adminId);

        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denúncia não encontrada."));

        if (!"PENDENTE".equals(denuncia.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta denúncia já foi resolvida.");
        }

        if (aceitar) {
            denuncia.setStatus("ACEITA");

            // Se o admin aceitou a denúncia, o conteúdo é inválido e deve ser deletado.
            // Precisamos desvincular a Foreign Key para manter o histórico da denúncia no banco.
            if (denuncia.getReclamacao() != null) {
                Long idAlvo = denuncia.getReclamacao().getId();
                denuncia.setReclamacao(null);
                denunciaRepository.save(denuncia); // Salva sem a trava de Foreign Key

                reclamacaoService.deletarReclamacaoPeloAdmin(idAlvo, adminId);
            }
            else if (denuncia.getComentario() != null) {
                Long idAlvo = denuncia.getComentario().getId();
                denuncia.setComentario(null);
                denunciaRepository.save(denuncia); // Salva sem a trava de Foreign Key

                comentarioService.deletarComentarioPeloAdmin(idAlvo, adminId);
            }
        } else {
            // Se a denúncia for falsa/exagerada, apenas a rejeitamos e o conteúdo original fica intacto.
            denuncia.setStatus("REJEITADA");
            denunciaRepository.save(denuncia);
        }

        return DenunciaResponse.de(denuncia);
    }

    // Helper method de segurança
    private void verificarAdmin(Long adminId) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        if (!"ADMIN".equalsIgnoreCase(admin.getTipo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado. Área restrita à moderação.");
        }
    }

}