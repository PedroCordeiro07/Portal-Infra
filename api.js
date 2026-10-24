// api.js
const API_BASE = "https://portal-infra-backend.onrender.com";

async function apiFetch(path, options = {}) {
  const resp = await fetch(API_BASE + path, {
    credentials: "include",          // envia o cookie JWT
    ...options,
  });
  if (resp.status === 401 || resp.status === 403) {
    localStorage.removeItem("usuario");
    // só redireciona se a página exigir login
    if (document.body.dataset.protegida === "true") {
      window.location.replace("login.html");
    }
  }
  return resp;
}
async function sessaoAtiva() {
  try {
    const r = await apiFetch("/usuarios/sessao");
    if (!r.ok) return false;
    const dados = await r.json();
    return dados.autenticado === true;
  } catch {
    return false;
  }
}