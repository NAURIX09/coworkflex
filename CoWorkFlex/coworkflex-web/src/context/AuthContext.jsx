import { createContext, useContext, useEffect, useState } from "react";
import client from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem("cw_user");
    return raw ? JSON.parse(raw) : null;
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (user) localStorage.setItem("cw_user", JSON.stringify(user));
    else localStorage.removeItem("cw_user");
  }, [user]);

  async function login(email, password) {
    setLoading(true);
    setError(null);
    try {
      const { data } = await client.post("/auth/login", { email, password });
      localStorage.setItem("cw_token", data.token);
      setUser(data.user ?? { id: data.userId, email, fullName: data.fullName });
      return true;
    } catch (err) {
      setError(
        err.response?.data?.message || "Email ou mot de passe incorrect."
      );
      return false;
    } finally {
      setLoading(false);
    }
  }

  async function register(payload) {
    setLoading(true);
    setError(null);
    try {
      await client.post("/auth/register", payload);
      return login(payload.email, payload.password);
    } catch (err) {
      setError(
        err.response?.data?.message || "Impossible de créer le compte."
      );
      return false;
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    localStorage.removeItem("cw_token");
    setUser(null);
  }

  return (
    <AuthContext.Provider
      value={{ user, login, register, logout, loading, error, isAdmin: user?.role === "ADMIN" }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth doit être utilisé dans <AuthProvider>");
  return ctx;
}
