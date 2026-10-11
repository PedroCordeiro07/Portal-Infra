
package org.example.portalinfra.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.example.portalinfra.dto.ReclamacaoRequest;
import org.example.portalinfra.dto.ReclamacaoResponse;
import org.example.portalinfra.model.Reclamacao;
import org.example.portalinfra.model.Usuario;
import org.example.portalinfra.repository.ReclamacaoRepository;
import org.example.portalinfra.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Map;


@Service
public class ReclamacaoService {

    private static final long TAMANHO_MAXIMO = 5L * 1024 * 1024;

    private final Cloudinary cloudinary;
    private final ReclamacaoRepository reclamacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public ReclamacaoService(
            Cloudinary cloudinary,
            ReclamacaoRepository reclamacaoRepository,
            UsuarioRepository usuarioRepository) {

        this.cloudinary = cloudinary;
        this.reclamacaoRepository = reclamacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ReclamacaoResponse criar(
            ReclamacaoRequest request,
            MultipartFile foto,
            Long usuarioId) {

        // A foto é obrigatória.
        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A foto da reclamação é obrigatória."
            );
        }

        if (foto.getSize() > TAMANHO_MAXIMO) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A foto deve ter no máximo 5 MB."
            );
        }

        String contentType = foto.getContentType();

        if (contentType == null ||
            !(contentType.equalsIgnoreCase("image/jpeg") ||
              contentType.equalsIgnoreCase("image/png"))) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Envie uma imagem JPG ou PNG."
            );
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Usuário não encontrado."
            ));

        if (!usuario.isEstaAtivo()) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Esta conta está desativada."
            );
        }

        try {
            Map resultado = cloudinary.uploader().upload(
                foto.getBytes(),
                ObjectUtils.asMap(
                    "folder", "portal-infra/reclamacoes",
                    "resource_type", "image"
                )
            );

            String fotoUrl = (String) resultado.get("secure_url");

            if (fotoUrl == null || fotoUrl.isBlank()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível obter a URL da imagem."
                );
            }

            Reclamacao reclamacao = new Reclamacao();

            reclamacao.setTitulo(request.titulo());
            reclamacao.setCategoria(request.categoria());
            reclamacao.setDescricao(request.descricao());
            reclamacao.setEndereco(request.endereco());
            reclamacao.setBairro(request.bairro());
            reclamacao.setGravidade(request.gravidade());
            reclamacao.setFotoUrl(fotoUrl);
            reclamacao.setStatus("Pendente");
            reclamacao.setUsuario(usuario);

            Reclamacao salva = reclamacaoRepository.save(reclamacao);

            return ReclamacaoResponse.de(salva);

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Erro ao enviar a imagem para o armazenamento."
            );
        }
    }

    @Transactional(readOnly = true)
    public Page<ReclamacaoResponse> listarTodas(Pageable pageable) {
        return reclamacaoRepository.findAllByOrderByDataCriacaoDesc(pageable)
                .stream()
                .map(ReclamacaoResponse::de);
    }

    @Transactional(readOnly = true)
    public Page<ReclamacaoResponse> listarPorUsuario(Long usuarioId,  Pageable pageable) {
        return reclamacaoRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioId, pageable)
                .stream()
                .map(ReclamacaoResponse::de);
    }


    // ==========================================
    // DELETAR RECLAMAÇÃO (USUÁRIO E ADMIN)
    // ==========================================

    @Transactional
    public void deletarPeloUsuario(Long id, Long usuarioId) {
        Reclamacao reclamacao = reclamacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reclamação não encontrada."));

        // Garante que um usuário não consiga apagar reclamações postadas por outros usuários
        if (!reclamacao.getUsuario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode apagar suas próprias reclamações.");
        }

        apagarDoCloudinaryEPostgres(reclamacao);
    }

    @Transactional
    public void deletarPeloAdmin(Long id, Long adminId) {
        Reclamacao reclamacao = reclamacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reclamação não encontrada."));

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        if (!"ADMIN".equalsIgnoreCase(admin.getTipo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado. Apenas administradores podem realizar esta ação.");
        }

        apagarDoCloudinaryEPostgres(reclamacao);
    }

    private void apagarDoCloudinaryEPostgres(Reclamacao reclamacao) {
        String publicId = extrairPublicId(reclamacao.getFotoUrl());

        try {
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, com.cloudinary.utils.ObjectUtils.emptyMap());
            }
        } catch (java.io.IOException e) {
            System.err.println("Erro ao deletar imagem do Cloudinary: " + e.getMessage());
        }

        reclamacaoRepository.delete(reclamacao);
    }

    // Extrai "portal-infra/reclamacoes/xyz123" da URL completa
    private String extrairPublicId(String url) {
        int inicio = url.indexOf("portal-infra/reclamacoes/");
        int fim = url.lastIndexOf(".");
        if (inicio != -1 && fim != -1) {
            return url.substring(inicio, fim);
        }
        return null;
    }

    @Transactional
    public ReclamacaoResponse atualizarStatus(Long id, String novoStatus, Long adminId) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário solicitante não encontrado."));

        if (!"ADMIN".equalsIgnoreCase(admin.getTipo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem atualizar o status da reclamação.");
        }

        Reclamacao reclamacao = reclamacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reclamação não encontrada."));

        if (!novoStatus.equals("Pendente") && !novoStatus.equals("Em Andamento") && !novoStatus.equals("Resolvido")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido.");
        }

        reclamacao.setStatus(novoStatus);
        return ReclamacaoResponse.de(reclamacaoRepository.save(reclamacao));
    }

}
