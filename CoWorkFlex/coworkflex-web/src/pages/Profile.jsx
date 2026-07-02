import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useReservations } from "../hooks/useReservations.js";
import StatusBadge from "../components/StatusBadge.jsx";
import Loader from "../components/Loader.jsx";
import { formatDateTime, formatPrice, isCancellable } from "../utils/dateUtils.js";

export default function Profile() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { reservations, loading, error, cancelReservation } = useReservations(user?.id);
  const [cancellingId, setCancellingId] = useState(null);
  const [actionError, setActionError] = useState(null);

  if (!user) {
    return (
      <div className="flex flex-col items-center gap-4 px-5 py-24 text-center">
        <i className="ti ti-lock text-4xl text-muted"></i>
        <p className="text-muted">Connectez-vous pour voir vos réservations.</p>
        <button onClick={() => navigate("/login")} className="btn-primary">
          Se connecter
        </button>
      </div>
    );
  }

  async function handleCancel(id) {
    setCancellingId(id);
    setActionError(null);
    try {
      await cancelReservation(id);
    } catch (err) {
      setActionError(
        err.response?.data?.message || "Annulation impossible (délai de 24h dépassé)."
      );
    } finally {
      setCancellingId(null);
    }
  }

  return (
    <div className="px-5 py-10 md:px-12">
      <h1 className="section-title mb-1">
        <i className="ti ti-ticket"></i> Mes réservations
      </h1>
      <p className="mb-8 text-sm text-muted">
        Annulation gratuite tant que la réservation démarre dans plus de 24h.
      </p>

      {actionError && (
        <div className="mb-4 flex items-center gap-2 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">
          <i className="ti ti-alert-circle"></i> {actionError}
        </div>
      )}

      {loading && <Loader label="Chargement de vos réservations..." />}

      {error && (
        <div className="flex items-center gap-2 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">
          <i className="ti ti-alert-circle"></i> {error}
        </div>
      )}

      {!loading && !error && reservations.length === 0 && (
        <div className="flex flex-col items-center gap-2 py-20 text-center text-muted">
          <i className="ti ti-ticket-off text-4xl"></i>
          <p>Vous n'avez aucune réservation pour le moment.</p>
        </div>
      )}

      {!loading && !error && reservations.length > 0 && (
        <div
          className="overflow-x-auto rounded-card border border-border bg-white"
          style={{ boxShadow: "var(--shadow)" }}
        >
          <table className="w-full min-w-[640px] text-left text-sm">
            <thead>
              <tr className="border-b border-border bg-gris text-xs uppercase tracking-wide text-muted">
                <th className="px-5 py-3 font-semibold">Poste</th>
                <th className="px-5 py-3 font-semibold">Début</th>
                <th className="px-5 py-3 font-semibold">Fin</th>
                <th className="px-5 py-3 font-semibold">Prix</th>
                <th className="px-5 py-3 font-semibold">Statut</th>
                <th className="px-5 py-3 font-semibold text-right">Action</th>
              </tr>
            </thead>
            <tbody>
              {reservations.map((r) => {
                const cancellable = r.status === "CONFIRMED" && isCancellable(r.startDateTime);
                return (
                  <tr key={r.id} className="border-b border-border last:border-0">
                    <td className="px-5 py-4 font-medium text-texte">
                      {r.deskLabel || r.desk?.label || `Poste #${r.deskId}`}
                    </td>
                    <td className="px-5 py-4 text-gray-600">{formatDateTime(r.startDateTime)}</td>
                    <td className="px-5 py-4 text-gray-600">{formatDateTime(r.endDateTime)}</td>
                    <td className="px-5 py-4 font-semibold text-rouge">
                      {formatPrice(r.totalPrice)}
                    </td>
                    <td className="px-5 py-4">
                      <StatusBadge status={r.status} />
                    </td>
                    <td className="px-5 py-4 text-right">
                      {r.status === "CONFIRMED" && (
                        <button
                          onClick={() => handleCancel(r.id)}
                          disabled={!cancellable || cancellingId === r.id}
                          title={!cancellable ? "Annulation impossible : début dans moins de 24h" : ""}
                          className="btn-outline !px-3.5 !py-1.5 text-[12px] disabled:cursor-not-allowed disabled:border-gray-200 disabled:text-gray-300"
                        >
                          {cancellingId === r.id ? (
                            <i className="ti ti-loader-2 spin"></i>
                          ) : (
                            <>
                              <i className="ti ti-x"></i> Annuler
                            </>
                          )}
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
