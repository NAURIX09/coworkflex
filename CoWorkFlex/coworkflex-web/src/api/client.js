import axios from "axios";

// L'URL du backend Spring Boot. Override possible via un fichier .env :
// VITE_API_URL=https://api.coworkflex.com/api
const baseURL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const client = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Injecte le JWT stocké au login sur chaque requête sortante
client.interceptors.request.use((config) => {
  const token = localStorage.getItem("cw_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Déconnexion automatique si le token est expiré/rejeté
client.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("cw_token");
      localStorage.removeItem("cw_user");
    }
    return Promise.reject(error);
  }
);

export default client;
