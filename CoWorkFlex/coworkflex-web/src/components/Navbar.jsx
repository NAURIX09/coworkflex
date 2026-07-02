import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

export default function Navbar() {
  const { user, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  const navLinks = [
    { to: "/", label: "ESPACES" },
    { to: "/profile", label: "MES RÉSERVATIONS" },
  ];

  const isActive = (to) =>
    to === "/" ? location.pathname === "/" : location.pathname.startsWith(to);

  return (
    <>
      <div className="bg-noir px-4 py-2 text-center text-xs tracking-wide text-white">
        <i className="ti ti-calendar-check"></i> Réservez un poste en moins de{" "}
        <strong>2 minutes</strong> — Annulation gratuite jusqu'à{" "}
        <span className="font-semibold text-rouge">24h avant</span>
      </div>

      <header className="sticky top-0 z-50 border-b border-border bg-white">
        <div className="flex h-16 items-center justify-between gap-5 px-5 md:px-12">
          <Link to="/" className="nav-logo">
            <i className="ti ti-building-skyscraper"></i>
            COWORK-FLEX
          </Link>

          <nav className="hidden gap-7 md:flex">
            {navLinks.map((l) => (
              <Link
                key={l.to}
                to={l.to}
                className="border-b-2 pb-1 text-[13px] font-semibold tracking-wide transition-colors"
                style={{
                  color: isActive(l.to) ? "var(--rouge)" : "#111",
                  borderColor: isActive(l.to) ? "var(--rouge)" : "transparent",
                }}
              >
                {l.label}
              </Link>
            ))}
          </nav>

          <div className="hidden items-center gap-4 md:flex">
            {user ? (
              <>
                <span className="text-sm text-gray-600">
                  Bonjour, <strong>{user.fullName || user.email}</strong>
                </span>
                <button onClick={logout} className="btn-outline">
                  <i className="ti ti-logout"></i> Déconnexion
                </button>
              </>
            ) : (
              <Link to="/login" className="btn-primary">
                <i className="ti ti-user"></i> Connexion
              </Link>
            )}
          </div>

          <button
            className="text-2xl text-gray-800 md:hidden"
            onClick={() => setMenuOpen(true)}
          >
            <i className="ti ti-menu-2"></i>
          </button>
        </div>
      </header>

      {menuOpen && (
        <div className="fixed inset-0 z-[100] flex flex-col overflow-y-auto bg-white p-6">
          <div className="mb-8 flex items-center justify-between">
            <span className="nav-logo">
              <i className="ti ti-building-skyscraper"></i> COWORK-FLEX
            </span>
            <button onClick={() => setMenuOpen(false)} className="text-2xl">
              <i className="ti ti-x"></i>
            </button>
          </div>

          {navLinks.map((l) => (
            <Link
              key={l.to}
              to={l.to}
              onClick={() => setMenuOpen(false)}
              className="flex items-center gap-3 border-b border-gray-100 py-4 text-base font-semibold"
              style={{ color: isActive(l.to) ? "var(--rouge)" : "#111" }}
            >
              {l.label}
            </Link>
          ))}

          <div className="mt-6 flex gap-4 border-t border-gray-100 pt-6">
            {user ? (
              <button
                onClick={() => {
                  logout();
                  setMenuOpen(false);
                }}
                className="btn-outline flex-1 justify-center"
              >
                Déconnexion
              </button>
            ) : (
              <Link
                to="/login"
                onClick={() => setMenuOpen(false)}
                className="btn-primary flex-1 justify-center"
              >
                Connexion
              </Link>
            )}
          </div>
        </div>
      )}
    </>
  );
}
