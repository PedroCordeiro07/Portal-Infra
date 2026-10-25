// =====================================================
// PROTEÇÃO DA PÁGINA E INICIALIZAÇÃO
// =====================================================
async function protegerPaginaReclamacoes() {
    const logado = await sessaoAtiva();
    if (!logado) {
        localStorage.removeItem("usuario");
        window.location.replace("login.html");
        return;
    }

    // Libera a tela com um fade suave após confirmar a sessão
    document.body.classList.add("autorizado");

    // Se estiver logado, dispara as chamadas iniciais com segurança
    carregarTotal();
    carregarReclamacoes();
}

protegerPaginaReclamacoes();


// =====================================================
// CONTAGEM DE RECLAMAÇÕES
// =====================================================
function mostrarTotal(total) {
    const el = document.getElementById('count-resultados');
    if (el) el.textContent = `${total.toLocaleString('pt-BR')} ${total === 1 ? 'reclamação' : 'reclamações'}`;
}

async function carregarTotal() {
    try {
        const salvo = localStorage.getItem('total_reclamacoes');
        if (salvo) mostrarTotal(Number(salvo));
    } catch (e) {}

    try {
        const res = await apiFetch('/reclamacoes/contagem');
        if (!res.ok) return;
        const d = await res.json();
        mostrarTotal(d.total);
        try { localStorage.setItem('total_reclamacoes', d.total); } catch (e) {}
    } catch (e) {
        console.error('Erro ao carregar total', e);
    }
}


// =====================================================
// FILTROS E PESQUISA
// =====================================================
let filtroAtual = new URLSearchParams();

function lerFiltros() {
    const v = (id) => (document.getElementById(id)?.value || '').trim();
    const p = new URLSearchParams();
    [['titulo', 'filtro-busca'], ['categoria', 'filtro-categoria'],
        ['bairro', 'filtro-bairro'], ['status', 'filtro-status']]
        .forEach(([nome, id]) => {
            const valor = v(id);
            if (valor && !valor.toLowerCase().startsWith('todos')) p.set(nome, valor);
        });
    return p;
}

function aplicarFiltro() {
    filtroAtual = lerFiltros();
    paginaAtual = 0;
    const lista = document.getElementById('lista-reclamacoes');
    if (lista) lista.innerHTML = '';
    carregarReclamacoes();
}

document.getElementById('btn-filtrar')?.addEventListener('click', aplicarFiltro);
document.getElementById('filtro-busca')?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') aplicarFiltro();
});


// =====================================================
// LISTAGEM DE RECLAMAÇÕES
// =====================================================
let paginaAtual = 0;

const esc = (t) => String(t ?? '').replace(/[&<>"']/g, c =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

function criarCard(r) {
    const classe = { pendente: 'pending', resolvida: 'resolved' }[(r.status || '').toLowerCase()] || '';
    const data = r.dataCriacao ? new Date(r.dataCriacao).toLocaleDateString('pt-BR') : '';
    return `
    <div class="complaint-card">
      <div class="image-container">
        <img src="${esc(r.fotoUrl)}" alt="${esc(r.titulo)}" />
        <span class="category">${esc(r.categoria)}</span>
      </div>
      <div class="card-content">
        <span class="status ${classe}">${esc(r.status)}</span>
        <h2>${esc(r.titulo)}</h2>
        <p class="location">${esc(r.endereco)}, ${esc(r.bairro)}</p>
        <p class="description">${esc(r.descricao)}</p>
        <div class="card-footer">
          <span>${data}</span>
          <button onclick="abrirComentarios(${Number(r.id)})">Ver detalhes</button>
        </div>
      </div>
    </div>`;
}

async function carregarReclamacoes() {
    const lista = document.getElementById('lista-reclamacoes');
    const btn = document.getElementById('btn-mais');
    const TAMANHO_PAGINA = 12;
    if (!lista) return;

    try {
        const params = new URLSearchParams(filtroAtual);
        params.set('page', paginaAtual);
        params.set('size', TAMANHO_PAGINA);

        const res = await apiFetch(`/reclamacoes?${params}`);
        if (!res.ok) return;
        const pagina = await res.json();

        if (paginaAtual === 0 && pagina.content.length === 0) {
            lista.innerHTML = '<p>Nenhuma reclamação encontrada.</p>';
        } else {
            lista.insertAdjacentHTML('beforeend', pagina.content.map(criarCard).join(''));
        }

        if (btn) btn.hidden = pagina.content.length < TAMANHO_PAGINA;
        paginaAtual++;
    } catch (e) {
        console.error('Erro ao carregar reclamações', e);
    }
}

const btnMais = document.getElementById('btn-mais');
if (btnMais) btnMais.addEventListener('click', carregarReclamacoes);


// =====================================================
// COMENTÁRIOS E THREADS
// =====================================================
async function carregarComentarios(reclamacaoId) {
    const container = document.getElementById("lista-comentarios");
    if (!container) return;

    try {
        const resposta = await apiFetch(`/comentarios/reclamacao/${reclamacaoId}`);
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

    if (comentario.respostas && comentario.respostas.length > 0) {
        const containerRespostas = div.querySelector(`#respostas-${comentario.id}`);
        comentario.respostas.forEach(filho => {
            containerRespostas.appendChild(criarElementoComentario(filho, reclamacaoId));
        });
    }

    return div;
}

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
        const form = new FormData();
        form.append("texto", texto);
        form.append("comentarioPaiId", comentarioPaiId);

        const resposta = await apiFetch(`/comentarios?reclamacaoId=${reclamacaoId}`, {
            method: "POST",
            body: form
        });

        if (!resposta.ok) throw new Error("Erro ao salvar resposta");

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
        const form = new FormData();
        form.append("texto", texto);

        const resposta = await apiFetch(`/comentarios?reclamacaoId=${reclamacaoAtualId}`, {
            method: "POST",
            body: form
        });

        if (!resposta.ok) throw new Error("Erro ao postar comentário");

        campo.value = "";
        carregarComentarios(reclamacaoAtualId);
    } catch (erro) {
        console.error("Erro:", erro);
        alert("Erro ao publicar comentário.");
    }
}