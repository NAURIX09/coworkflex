import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { createReservation } from "../hooks/useReservations.js";
import { formatPrice, hoursBetween } from "../utils/dateUtils.js";

function toLocalInputValue(date) {
  const d = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return d.toISOString().slice(0, 16);
}

export default function ReservationModal({ desk, onClose, onSuccess }) {
  const { user } = useAuth();
  const navigate = useNavigate();

  const defaultStart = useMemo(() => {
    const d = new Date();
    d.setMinutes(0, 0, 0);
    d.setHours(d.getHours() + 1);
    return d;
  }, []);
  const defaultEnd = useMemo(() => {
    const d = new Date(defaultStart);
    d.setHours(d.getHours() + 2);
    return d;
  }, [defaultStart]);

  const [start, setStart] = useState(toLocalInputValue(defaultStart));
  const [end, setEnd] = useState(toLocalInputValue(defaultEnd));
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const hours = hoursBetween(start, end);
  const estimatedPrice = hours * Number(desk.pricePerHour);
  const isValidRange = hours > 0;

  async function handleSubmit(e) {
    e.preventDefault();
    if (!user) {
      onClose();
      navigate("/login");
      return;
    }
    if (!isValidRange) {
      setError("L'heure de fin doit être après l'heure de début.");
      return;
    }

    setSubmitting(true);
    setError(null);
    const result = await createReservation({
      deskId: desk.id,
      startDateTime: new Date(start).toISOString(),
      endDateTime: new Date(end).toISOString(),
    });
    setSubmitting(false);

    if (result.ok) {
      setSuccess(true);
      onSuccess?.(result.data);
      setTimeout(onClose, 1200);
    } else {
      setError(result.error);
    }
  }

  return (
    <div
      className="fixed inset-0 z-[200] flex items-center justify-center p-4"
      style={{ background: "rgba(10,10,10,0.45)", backdropFilter: "blur(6px)" }}
      onClick={onClose}
    >
      <div
        className="fade-in card w-full max-w-md p-6"
        style={{ boxShadow: "var(--shadow-hover)" }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="mb-5 flex items-start justify-between">
          <div>
            <h2 className="font-title text-2xl tracking-wide text-texte">
              Réserver — {desk.label}
            </h2>
            <p className="text-sm text-muted">
              {formatPrice(desk.pricePerHour)} / heure
            </p>
          </div>
          <button onClick={onClose} className="text-2xl text-gray-400 hover:text-texte">
            <i className="ti ti-x"></i>
          </button>
        </div>

        {success ? (
          <div className="flex flex-col items-center gap-2 py-8 text-center">
            <i className="ti ti-circle-check text-5xl text-green-500"></i>
            <p className="font-semibold text-texte">Réservation confirmée !</p>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="form-group">
            <label>Début</label>
            <input
              type="datetime-local"
              value={start}
              onChange={(e) => setStart(e.target.value)}
              required
            />

            <label className="mt-3">Fin</label>
            <input
              type="datetime-local"
              value={end}
              onChange={(e) => setEnd(e.target.value)}
              required
            />

            <div className="my-4 flex items-center justify-between rounded-[10px] bg-gris px-4 py-3">
              <span className="text-sm text-gray-600">
                {hours > 0 ? `${hours} h × ${formatPrice(desk.pricePerHour)}` : "Choisissez un créneau"}
              </span>
              <span className="text-lg font-bold text-rouge">
                {hours > 0 ? formatPrice(estimatedPrice) : "—"}
              </span>
            </div>

            {error && (
              <div className="mb-4 flex items-center gap-2 rounded-lg bg-red-50 px-3.5 py-2.5 text-[13px] text-red-600">
                <i className="ti ti-alert-circle"></i> {error}
              </div>
            )}

            <button
              type="submit"
              disabled={submitting || !isValidRange}
              className="btn-primary w-full justify-center"
            >
              {submitting ? (
                <>
                  <i className="ti ti-loader-2 spin"></i> Traitement...
                </>
              ) : (
                <>
                  <i className="ti ti-calendar-check"></i> Confirmer la réservation
                </>
              )}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
