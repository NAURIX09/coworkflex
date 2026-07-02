// Formatte une date ISO en libellé court FR : "12 juil. 2026, 14:00"
export function formatDateTime(iso) {
  if (!iso) return "—";
  return new Date(iso).toLocaleString("fr-FR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function formatDate(iso) {
  if (!iso) return "—";
  return new Date(iso).toLocaleDateString("fr-FR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

export function formatPrice(value) {
  if (value === null || value === undefined) return "—";
  return new Intl.NumberFormat("fr-FR").format(value) + " FCFA";
}

// Nombre d'heures pleines entre deux dates (utilisé pour l'estimation du prix côté front)
export function hoursBetween(start, end) {
  if (!start || !end) return 0;
  const ms = new Date(end) - new Date(start);
  return Math.max(0, Math.round(ms / 3_600_000));
}

// Reflète la règle métier backend : annulation impossible si le début
// de la réservation est dans moins de 24h.
export function isCancellable(startDateTime) {
  if (!startDateTime) return false;
  const deadline = new Date(startDateTime).getTime() - 24 * 3_600_000;
  return Date.now() < deadline;
}

export function hoursUntil(startDateTime) {
  const ms = new Date(startDateTime) - Date.now();
  return ms / 3_600_000;
}
