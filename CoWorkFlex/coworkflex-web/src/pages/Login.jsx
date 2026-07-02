import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function Login() {
  const { login, register, loading, error } = useAuth();
  const navigate = useNavigate();
  const [mode, setMode] = useState("login"); // "login" | "register"
  const [form, setForm] = useState({ email: "", password: "", fullName: "" });

  function update(field) {
    return (e) => setForm((f) => ({ ...f, [field]: e.target.value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const ok =
      mode === "login"
        ? await login(form.email, form.password)
        : await register(form);
    if (ok) navigate("/");
  }

  return (
    <div className="grid min-h-[calc(100vh-104px)] grid-cols-1 md:grid-cols-2">
      <div className="hidden flex-col justify-center bg-noir p-16 text-white md:flex">
        <i className="ti ti-building-skyscraper mb-6 text-5xl text-rouge"></i>
        <h1 className="font-title text-5xl leading-tight tracking-wide">
          RÉSERVEZ VOTRE
          <br />
          ESPACE DE TRAVAIL
        </h1>
        <p className="mt-4 max-w-sm text-sm text-white/60">
          Bureaux privés, salles de réunion et open spaces disponibles à la
          demande, dans tous vos espaces de coworking favoris.
        </p>
      </div>

      <div className="flex min-h-[calc(100vh-104px)] flex-col justify-center px-6 py-12 md:px-16">
        <div className="mx-auto w-full max-w-sm">
          <h2 className="mb-1 text-2xl font-semibold text-texte">
            {mode === "login" ? "Bon retour !" : "Créer un compte"}
          </h2>
          <p className="mb-6 text-sm text-muted">
            {mode === "login"
              ? "Connectez-vous pour gérer vos réservations."
              : "Rejoignez CoWork-Flex en quelques secondes."}
          </p>

          <form onSubmit={handleSubmit} className="form-group">
            {mode === "register" && (
              <>
                <label>Nom complet</label>
                <input
                  value={form.fullName}
                  onChange={update("fullName")}
                  placeholder="Aya Koné"
                  required
                />
              </>
            )}

            <label className={mode === "register" ? "mt-3" : ""}>Email</label>
            <input
              type="email"
              value={form.email}
              onChange={update("email")}
              placeholder="vous@exemple.com"
              required
            />

            <label className="mt-3">Mot de passe</label>
            <input
              type="password"
              value={form.password}
              onChange={update("password")}
              placeholder="••••••••"
              required
            />

            {error && (
              <div className="my-4 flex items-center gap-2 rounded-lg bg-red-50 px-3.5 py-2.5 text-[13px] text-red-600">
                <i className="ti ti-alert-circle"></i> {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="btn-primary mt-5 w-full justify-center"
            >
              {loading ? (
                <i className="ti ti-loader-2 spin"></i>
              ) : mode === "login" ? (
                "Se connecter"
              ) : (
                "Créer mon compte"
              )}
            </button>
          </form>

          <p className="mt-6 text-center text-sm text-muted">
            {mode === "login" ? "Pas encore de compte ?" : "Déjà inscrit ?"}{" "}
            <button
              onClick={() => setMode(mode === "login" ? "register" : "login")}
              className="font-semibold text-rouge"
            >
              {mode === "login" ? "Inscrivez-vous" : "Connectez-vous"}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}
