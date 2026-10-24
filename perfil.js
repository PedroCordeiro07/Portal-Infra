


document.addEventListener("DOMContentLoaded", async function () {
    const usuarioLogado = localStorage.getItem("usuarioLogado");
    const usuario = JSON.parse(localStorage.getItem("usuario") || "{}");

    if (!(await sessaoAtiva())) {
    window.location.replace("login.html");
    return;   // no perfil.js; no reclamacao.js, veja a nota abaixo
    }

    const formulario = document.getElementById("formularioPerfil");
    const botaoAlterar = document.getElementById("botaoAlterar");
    const botaoSalvar = document.getElementById("botaoSalvar");
    const botaoCancelar = document.getElementById("botaoCancelar");

    if (!formulario || !botaoAlterar || !botaoSalvar || !botaoCancelar) {
        console.error("Verifique os IDs do formulário e dos botões no HTML.");
        return;
    }

    // Relaciona os campos da API com os IDs do HTML.
    const campos = {
        nome: "nome",
        email: "email",
        telefone: "telefone",
        dataNascimento: "dataNascimento",
        cep: "cep",
        logradouro: "logradouro",
        numero: "numero",
        complemento: "complemento",
        bairro: "bairro",
        cidade: "cidade",
        uf: "estado"
    };

    // Campos editáveis nesta tela.
    // O e-mail possui um processo próprio de verificação.
    const camposEditaveis = [
        "nome",
        "telefone",
        "dataNascimento",
        "cep",
        "logradouro",
        "numero",
        "complemento",
        "bairro",
        "cidade",
        "estado"
    ];

    const inputsEditaveis = camposEditaveis
        .map(id => document.getElementById(id))
        .filter(input => input !== null);

    let valoresOriginais = {};

    function guardarValores() {
        valoresOriginais = {};

        inputsEditaveis.forEach(input => {
            valoresOriginais[input.id] = input.value;
        });
    }

    function definirModoEdicao(editar) {
        inputsEditaveis.forEach(input => {
            input.disabled = !editar;
        });

        botaoAlterar.style.display = editar ? "none" : "inline-block";
        botaoSalvar.style.display = editar ? "inline-block" : "none";
        botaoCancelar.style.display = editar ? "inline-block" : "none";
    }

    // Carrega os dados atuais do banco.
    try {
        const resposta = await apiFetch("/usuarios/me");
        if (!resposta.ok) {
            throw new Error("Não foi possível carregar seu perfil.");
        }

        const perfil = await resposta.json();

        for (const [campoAPI, idHTML] of Object.entries(campos)) {
            const input = document.getElementById(idHTML);

            if (input) {
                input.value = perfil[campoAPI] ?? "";
            }
        }

        guardarValores();
        definirModoEdicao(false);

    } catch (erro) {
        console.error("Erro ao carregar perfil:", erro);
        alert("Não foi possível carregar seus dados. Tente novamente.");
        return;
    }

    // Habilita a edição.
    botaoAlterar.addEventListener("click", function () {
        guardarValores();
        definirModoEdicao(true);
    });

    // Cancela as alterações.
    botaoCancelar.addEventListener("click", function () {
        inputsEditaveis.forEach(input => {
            input.value = valoresOriginais[input.id] ?? "";
        });

        definirModoEdicao(false);
    });

    // Envia os dados para o backend.
    formulario.addEventListener("submit", async function (evento) {
        evento.preventDefault();

            
    function obterValor(id) {
        const campo = document.getElementById(id);

        if (!campo) {
            throw new Error(
                `Campo com id="${id}" não encontrado no HTML do perfil.`
            );
        }

        return campo.value.trim();
    }

    const payload = {
        nome: obterValor("nome"),
        telefone: obterValor("telefone").replace(/\D/g, ""),
        dataNascimento: obterValor("dataNascimento") || null,
        cep: obterValor("cep").replace(/\D/g, ""),
        logradouro: obterValor("logradouro"),
        numero: obterValor("numero"),
        complemento: obterValor("complemento"),
        bairro: obterValor("bairro"),
        cidade: obterValor("cidade"),
        uf: obterValor("estado").toUpperCase()
    };

        botaoSalvar.disabled = true;
        botaoSalvar.textContent = "Salvando...";

        try {
           const resposta = await apiFetch("/usuarios/me/perfil", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            const resultado = await resposta.json().catch(() => ({}));

            if (!resposta.ok) {
                throw new Error(
                    resultado.erro ||
                    resultado.message ||
                    "Verifique os dados e o endpoint de atualização."
                );
            }

            // Atualiza a tela com os dados retornados pelo backend.
            for (const [campoAPI, idHTML] of Object.entries(campos)) {
                const input = document.getElementById(idHTML);

                if (input && resultado[campoAPI] !== undefined) {
                    input.value = resultado[campoAPI] ?? "";
                }
            }

            guardarValores();
            definirModoEdicao(false);

            // Atualiza os dados locais básicos, se necessário.
            const usuarioAtualizado = {
                ...usuario,
                nome: resultado.nome ?? payload.nome
            };

            localStorage.setItem(
                "usuario",
                JSON.stringify(usuarioAtualizado)
            );

            alert("Informações atualizadas com sucesso!");

        } catch (erro) {
            console.error("Erro ao salvar perfil:", erro);
            alert("Não foi possível salvar: " + erro.message);

        } finally {
            botaoSalvar.disabled = false;
            botaoSalvar.textContent = "Salvar alterações";
        }
    });

    
const botaoAlterarSenha = document.getElementById("botaoAlterarSenha");
const formularioSenha = document.getElementById("formularioSenha");
const botaoCancelarSenha = document.getElementById("botaoCancelarSenha");
const botaoSalvarSenha = document.getElementById("botaoSalvarSenha");
const mensagemSenha = document.getElementById("mensagemSenha");

function mostrarMensagemSenha(texto, tipo) {
    mensagemSenha.textContent = texto;
    mensagemSenha.className = `mensagem-senha ${tipo}`;
    mensagemSenha.hidden = false;
}

function limparFormularioSenha() {
    formularioSenha.reset();
    mensagemSenha.textContent = "";
    mensagemSenha.hidden = true;
    mensagemSenha.className = "mensagem-senha";

    formularioSenha.querySelectorAll("[data-campo-senha]").forEach(botao => {
        const campo = document.getElementById(botao.dataset.campoSenha);
        campo.type = "password";
        botao.textContent = "Mostrar";
        botao.setAttribute(
            "aria-label",
            `Mostrar ${botao.dataset.campoSenha === "senhaAtual"
                ? "senha atual"
                : botao.dataset.campoSenha === "novaSenha"
                    ? "nova senha"
                    : "confirmação da senha"}`
        );
    });
}

botaoAlterarSenha?.addEventListener("click", () => {
    const vaiAbrir = formularioSenha.hidden;

    formularioSenha.hidden = !vaiAbrir;
    botaoAlterarSenha.setAttribute("aria-expanded", String(vaiAbrir));

    if (vaiAbrir) {
        document.getElementById("senhaAtual").focus();
    } else {
        limparFormularioSenha();
    }
});

botaoCancelarSenha?.addEventListener("click", () => {
    limparFormularioSenha();
    formularioSenha.hidden = true;
    botaoAlterarSenha.setAttribute("aria-expanded", "false");
});

formularioSenha?.querySelectorAll("[data-campo-senha]").forEach(botao => {
    botao.addEventListener("click", () => {
        const campo = document.getElementById(botao.dataset.campoSenha);
        const vaiMostrar = campo.type === "password";

        campo.type = vaiMostrar ? "text" : "password";
        botao.textContent = vaiMostrar ? "Ocultar" : "Mostrar";

        const nomes = {
            senhaAtual: "senha atual",
            novaSenha: "nova senha",
            confirmarSenha: "confirmação da senha"
        };

        botao.setAttribute(
            "aria-label",
            `${vaiMostrar ? "Ocultar" : "Mostrar"} ${nomes[campo.id]}`
        );
    });
});

formularioSenha?.addEventListener("submit", async evento => {
    evento.preventDefault();

    const senhaAtual = document.getElementById("senhaAtual").value;
    const novaSenha = document.getElementById("novaSenha").value;
    const confirmarSenha = document.getElementById("confirmarSenha").value;

    if (novaSenha.length < 6) {
        mostrarMensagemSenha(
            "A nova senha deve ter pelo menos 6 caracteres.",
            "erro"
        );
        return;
    }

    if (novaSenha !== confirmarSenha) {
        mostrarMensagemSenha(
            "A confirmação não corresponde à nova senha.",
            "erro"
        );
        return;
    }

    if (senhaAtual === novaSenha) {
        mostrarMensagemSenha(
            "A nova senha deve ser diferente da senha atual.",
            "erro"
        );
        return;
    }

    botaoSalvarSenha.disabled = true;
    botaoCancelarSenha.disabled = true;
    botaoSalvarSenha.textContent = "Salvando...";

    try {
        const resposta = await apiFetch("/usuarios/me/senha", {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                senhaAtual,
                novaSenha
            })
        });

        const resultado = await resposta.json().catch(() => ({}));

        if (!resposta.ok) {
            throw new Error(
                resultado.erro || "Não foi possível alterar a senha."
            );
        }

        limparFormularioSenha();

        mostrarMensagemSenha(
            resultado.mensagem || "Senha alterada com sucesso!",
            "sucesso"
        );

    } catch (erro) {
        mostrarMensagemSenha(
            erro.message || "Ocorreu um erro ao alterar a senha.",
            "erro"
        );
    } finally {
        botaoSalvarSenha.disabled = false;
        botaoCancelarSenha.disabled = false;
        botaoSalvarSenha.textContent = "Salvar nova senha";
    }
});



    const botaoExcluirConta = document.getElementById("botaoExcluirConta");

    botaoExcluirConta?.addEventListener("click", async function () {
        const primeiraConfirmacao = confirm(
            "Deseja realmente desativar sua conta?\n\n" +
            "Se continuar, sua conta será desativada."
        );

        if (!primeiraConfirmacao) {
            return;
        }

        const segundaConfirmacao = confirm(
            "ATENÇÃO!\n\n" +
            "Você não poderá mais entrar nessa conta após a desativação.\n" +
            "Seus dados permanecerão armazenados.\n\n" +
            "Tem certeza de que deseja continuar?"
        );

        if (!segundaConfirmacao) {
            alert("Desativação cancelada.");
            return;
        }
        const senha = prompt("Digite sua senha atual para confirmar:");

        if (senha === null) return;

        if (!senha.trim()) {
            alert("Informe sua senha para continuar.");
            return;
        }

        botaoExcluirConta.disabled = true;
        botaoExcluirConta.textContent = "Desativando...";

        try {
            const resposta = await apiFetch("/usuarios/me/desativar", {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ senha })
            });

            const resultado = await resposta.json().catch(() => ({}));

            if (!resposta.ok) {
                throw new Error(
                    resultado.erro || "Não foi possível desativar a conta."
                );
            }

            localStorage.removeItem("usuario");
            localStorage.removeItem("usuarioLogado");

            alert("Sua conta foi desativada com sucesso.");

            window.location.replace("login.html");

        } catch (erro) {
            console.error("Erro ao desativar conta:", erro);
            alert(erro.message);

        } finally {
            botaoExcluirConta.disabled = false;
            botaoExcluirConta.textContent = "Desativar minha conta";
        }
    });

});
