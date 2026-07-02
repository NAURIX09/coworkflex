import { useCallback, useEffect, useState } from "react";
import client from "../api/client";

export function useReservations(userId) {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchReservations = useCallback(async () => {
    if (!userId) return;
    setLoading(true);
    setError(null);
    try {
      const { data } = await client.get(`/reservations/user/${userId}`);
      setReservations(data);
    } catch (err) {
      setError(err.response?.data?.message || "Impossible de charger vos réservations.");
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchReservations();
  }, [fetchReservations]);

  async function cancelReservation(id) {
    await client.delete(`/reservations/${id}`);
    setReservations((prev) =>
      prev.map((r) => (r.id === id ? { ...r, status: "CANCELLED" } : r))
    );
  }

  return { reservations, loading, error, refetch: fetchReservations, cancelReservation };
}

// Création d'une réservation. Renvoie { ok, error, conflict } pour que la modale
// puisse distinguer un 409 (créneau déjà pris) d'une autre erreur.
export async function createReservation({ deskId, startDateTime, endDateTime }) {
  try {
    const { data } = await client.post("/reservations", {
      deskId,
      startDateTime,
      endDateTime,
    });
    return { ok: true, data };
  } catch (err) {
    const status = err.response?.status;
    const message =
      err.response?.data?.message ||
      (status === 409
        ? "Ce poste est déjà réservé sur ce créneau."
        : "La réservation a échoué. Réessayez.");
    return { ok: false, error: message, conflict: status === 409 };
  }
}
