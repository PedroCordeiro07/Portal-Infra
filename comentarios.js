const API_BASE = "https://portal-infra-backend.onrender.com";

// Carrega e desenha os comentários de uma reclamação
async function carregarComentarios(reclamacaoId) {
    const container = document.getElementById("lista-comentarios");
    if (!container) return;

    try {
        const resposta = await fetch(`${API_BASE}/comentarios/reclamacao/${reclamacaoId}`);
        if (!resposta.ok) throw new Error("Erro ao carregar comentários");

        const comentarios = await resposta.json();
        container.innerHTML = "";

        if (comentarios.length === 0) {
            container.innerHTML = "<p style='color: #888; font-size: 13px;'>Nenhum comentário ainda. Seja o primeiro a comentar!</p>";
            return;
        }

        comentarios.forEach(comentario => {
            container.appendChild(criarElementoComentario(comentario, reclamacaoId));
        });
    } catch (erro) {
        console.error("Erro:", erro);
        container.innerHTML = "<p style='color: red; font-size: 13px;'>Erro ao carregar comentários.</p>";
    }
}

// Cria o elemento visual de um comentário e suas respostas encadeadas
function criarElementoComentario(comentario, reclamacaoId) {
    const div = document.createElement("div");
    div.className = "comentario-item";

    const dataFormatada = comentario.dataCriacao ? new Date(comentario.dataCriacao).toLocaleDateString("pt-BR") : "";

    div.innerHTML = `
        <div class="comentario-header">
            <span class="comentario-autor">${comentario.usuarioNome || 'Usuário'}</span>
            <span class="comentario-data" style="color: #888; font-size: 11px; margin-left: 8px;">${dataFormatada}</span>
        </div>
        <div class="comentario-texto">${comentario.texto}</div>
        <button class="btn-responder" onclick="mostrarFormularioResposta(${comentario.id}, ${reclamacaoId})">Responder</button>
        <div id="caixa-resposta-${comentario.id}" style="display: none; margin-top: 8px;"></div>
        <div class="respostas-container" id="respostas-${comentario.id}"></div>
    `;

    // Se tiver respostas aninhadas (thread estilo Twitter)
    if (comentario.respostas && comentario.respostas.length > 0) {
        const containerRespostas = div.querySelector(`#respostas-${comentario.id}`);
        comentario.respostas.forEach(filho => {
            containerRespostas.appendChild(criarElementoComentario(filho, reclamacaoId));
        });
    }

    return div;
}

// Abre uma caixinha para responder diretamente àquele comentário
function mostrarFormularioResposta(comentarioId, reclamacaoId) {
    const caixa = document.getElementById(`caixa-resposta-${comentarioId}`);
    if (!caixa) return;

    if (caixa.style.display === "block") {
        caixa.style.display = "none";
        return;
    }

    caixa.style.display = "block";
    caixa.innerHTML = `
        <textarea id="texto-resposta-${comentarioId}" placeholder="Escreva sua resposta..." style="width: 100%; border-radius: 6px; border: 1px solid #ccc; padding: 6px; font-size: 13px;"></textarea>
        <div style="margin-top: 4px;">
            <button onclick="enviarResposta(${comentarioId}, ${reclamacaoId})" style="background: #2563EB; color: white; border: none; border-radius: 4px; padding: 4px 10px; cursor: pointer; font-size: 12px;">Enviar resposta</button>
            <button onclick="document.getElementById('caixa-resposta-${comentarioId}').style.display='none'" style="background: none; border: none; color: #666; cursor: pointer; font-size: 12px; margin-left: 6px;">Cancelar</button>
        </div>
    `;
}

// Envia a resposta do comentário para o backend
async function enviarResposta(comentarioPaiId, reclamacaoId) {
    const usuario = JSON.parse(localStorage.getItem("usuario") || "{}");
    if (!usuario.id) {
        alert("Você precisa estar logado para comentar!");
        return;
    }

    const campoTexto = document.getElementById(`texto-resposta-${comentarioPaiId}`);
    const texto = campoTexto ? campoTexto.value.trim() : "";
    if (!texto) return;

    try {
        const resposta = await fetch(`${API_BASE}/comentarios?usuarioId=${usuario.id}&reclamacaoId=${reclamacaoId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                texto: texto,
                comentarioPaiId: comentarioPaiId
            })
        });

        if (!resposta.ok) throw new Error("Erro ao salvar resposta");

        // Recarrega os comentários para já exibir a thread atualizada
        carregarComentarios(reclamacaoId);
    } catch (erro) {
        console.error("Erro:", erro);
        alert("Não foi possível enviar a resposta.");
    }
}

// --- CONTROLE DO MODAL DE COMENTÁRIOS ---
let reclamacaoAtualId = null;

function abrirComentarios(reclamacaoId) {
    reclamacaoAtualId = reclamacaoId;
    document.getElementById("modal-comentarios").style.display = "flex";
    carregarComentarios(reclamacaoId);
}

function fecharModalComentarios() {
    document.getElementById("modal-comentarios").style.display = "none";
}

async function enviarComentarioPrincipal() {
    const usuario = JSON.parse(localStorage.getItem("usuario") || "{}");
    if (!usuario.id) {
        alert("Você precisa estar logado para comentar!");
        return;
    }

    const campo = document.getElementById("novo-comentario-texto");
    const texto = campo ? campo.value.trim() : "";
    if (!texto) return;

    try {
        const resposta = await fetch(`${API_BASE}/comentarios?usuarioId=${usuario.id}&reclamacaoId=${reclamacaoAtualId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                texto: texto,
                comentarioPaiId: null
            })
        });

        if (!resposta.ok) throw new Error("Erro ao postar comentário");

        campo.value = "";
        carregarComentarios(reclamacaoAtualId);
    } catch (erro) {
        console.error("Erro:", erro);
        alert("Erro ao publicar comentário.");
    }
}
