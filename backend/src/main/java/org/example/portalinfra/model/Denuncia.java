package org.example.portalinfra.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "denuncias")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // O usuário que fez a denúncia

    @ManyToOne
    @JoinColumn(name = "reclamacao_id")
    private Reclamacao reclamacao; // Pode ser null se a denúncia for de um comentário

    @ManyToOne
    @JoinColumn(name = "comentario_id")
    private Comentario comentario; // Pode ser null se a denúncia for de uma reclamação

    @Column(nullable = false)
    private String tipo; // Ex: DISCURSO_DE_ODIO

    @Column(length = 1000)
    private String descricao; // Opcional

    @Column(nullable = false)
    private String status = "PENDENTE"; // PENDENTE, ACEITA, REJEITADA

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Reclamacao getReclamacao() { return reclamacao; }
    public void setReclamacao(Reclamacao reclamacao) { this.reclamacao = reclamacao; }
    public Comentario getComentario() { return comentario; }
    public void setComentario(Comentario comentario) { this.comentario = comentario; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}