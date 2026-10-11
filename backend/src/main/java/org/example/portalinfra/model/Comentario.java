package org.example.portalinfra.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "texto", nullable = false, length = 1000)
    private String texto;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reclamacao_id", nullable = false)
    private Reclamacao reclamacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_pai_id")
    private Comentario comentarioPai;

    @OneToMany(mappedBy = "comentarioPai", cascade = CascadeType.ALL)
    private List<Comentario> respostas = new ArrayList<>();

    public Comentario() {}

    @PrePersist
    protected void antesDeSalvar() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Reclamacao getReclamacao() { return reclamacao; }
    public void setReclamacao(Reclamacao reclamacao) { this.reclamacao = reclamacao; }

    public Comentario getComentarioPai() { return comentarioPai; }
    public void setComentarioPai(Comentario comentarioPai) { this.comentarioPai = comentarioPai; }

    public List<Comentario> getRespostas() { return respostas; }
    public void setRespostas(List<Comentario> respostas) { this.respostas = respostas; }
}
