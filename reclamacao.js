// =====================================================
// PROTEÇÃO DA PÁGINA (Evita piscar)
// =====================================================
async function protegerPaginaNovaReclamacao() {
    const logado = await sessaoAtiva();
    if (!logado) {
        localStorage.removeItem("usuario");
        window.location.replace("login.html");
        return;
    }

    // Libera a tela com um fade suave após confirmar a sessão
    document.body.classList.add("autorizado");
}

// Executa a verificação imediatamente
protegerPaginaNovaReclamacao();


// =====================================================
// LÓGICA DO FORMULÁRIO
// =====================================================
const formulario = document.getElementById("form-reclamacao");
const mensagem = document.getElementById("mensagem-formulario");
const campoFoto = document.getElementById("foto");

formulario.addEventListener("submit", async function (event) {
    event.preventDefault();
    mensagem.textContent = "";
    mensagem.style.color = ""; // Reseta a cor

    const botao = formulario.querySelector('button[type="submit"]');
    const foto = campoFoto.files[0];

    // Validação da foto obrigatória.
    if (!foto) {
        mensagem.textContent = "Selecione uma foto do problema.";
        mensagem.style.color = "#ef4444"; // Vermelho erro
        campoFoto.focus();
        return;
    }

    const formatosValidos = ["image/jpeg", "image/png"];
    if (!formatosValidos.includes(foto.type)) {
        mensagem.textContent = "Envie uma foto JPG ou PNG.";
        mensagem.style.color = "#ef4444";
        campoFoto.value = "";
        return;
    }

    // Validação de tamanho (5MB máximo)
    if (foto.size > 5 * 1024 * 1024) {
        mensagem.textContent = "A foto deve ter no máximo 5 MB.";
        mensagem.style.color = "#ef4444";
        campoFoto.value = "";
        return;
    }

    // Recupera o usuário salvo no navegador
    const usuario = JSON.parse(localStorage.getItem("usuario") || "{}");
    if (!usuario.id) {
        mensagem.textContent = "Não foi possível identificar sua conta. Entre novamente.";
        mensagem.style.color = "#ef4444";
        return;
    }

    const dados = {
        titulo: document.getElementById("titulo").value.trim(),
        categoria: document.getElementById("categoria").value,
        descricao: document.getElementById("descricao").value.trim(),
        endereco: document.getElementById("endereco").value.trim(),
        bairro: document.getElementById("bairro").value,
        gravidade: document.getElementById("gravidade").value
    };

    const formData = new FormData();

    // Parte JSON com os dados da reclamação.
    formData.append(
        "dados",
        new Blob([JSON.stringify(dados)], { type: "application/json" })
    );

    // Parte com o arquivo de imagem.
    formData.append("foto", foto);

    botao.disabled = true;
    botao.textContent = "Enviando...";

    try {
        const resposta = await apiFetch("/reclamacoes", {
            method: "POST",
            body: formData
        });

        if (!resposta.ok) {
            const erro = await resposta.text();
            throw new Error(erro || `Erro ao cadastrar (${resposta.status}).`);
        }

        const reclamacao = await resposta.json();

        mensagem.textContent = `Reclamação #${reclamacao.id} cadastrada com sucesso!`;
        mensagem.style.color = "#10b981"; // Verde sucesso
        formulario.reset();

    } catch (erro) {
        console.error("Erro ao enviar reclamação:", erro);
        mensagem.textContent = "Não foi possível enviar a reclamação. Verifique a conexão e tente novamente.";
        mensagem.style.color = "#ef4444"; // Vermelho erro

    } finally {
        botao.disabled = false;
        botao.textContent = "Enviar reclamação";
    }
});