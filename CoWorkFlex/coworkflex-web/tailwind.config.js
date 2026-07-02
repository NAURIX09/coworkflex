/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        rouge: "#E8194B",
        "rouge-dark": "#c4143f",
        "rouge-light": "#fff0f4",
        noir: "#0a0a0a",
        gris: "#f4f4f4",
        gris2: "#e8e8e8",
        texte: "#1a1a1a",
        muted: "#888888",
        border: "#e5e5e5",
        violet: "#a855f7",
      },
      fontFamily: {
        title: ["Bebas Neue", "sans-serif"],
        body: ["Inter", "sans-serif"],
      },
      borderRadius: {
        card: "12px",
      },
      boxShadow: {
        card: "0 4px 24px rgba(0,0,0,.08)",
        "card-hover": "0 12px 40px rgba(0,0,0,.15)",
      },
      keyframes: {
        fadeIn: {
          from: { opacity: 0, transform: "translateY(16px)" },
          to: { opacity: 1, transform: "translateY(0)" },
        },
      },
      animation: {
        fadeIn: "fadeIn .4s ease",
      },
    },
  },
  plugins: [],
};
