import { formatPrice } from "../utils/dateUtils";

const TYPE_META = {
  OPEN_SPACE: { label: "Open Space", cls: "badge-blue", icon: "ti-layout-grid" },
  REUNION: { label: "Salle de réunion", cls: "badge-orange", icon: "ti-users-group" },
  PRIVE: { label: "Bureau privé", cls: "badge-violet", icon: "ti-lock" },
};

export default function DeskCard({ desk, onReserve }) {
  const meta = TYPE_META[desk.type] || TYPE_META.OPEN_SPACE;
  const amenities = (desk.amenities || "")
    .split(",")
    .map((a) => a.trim())
    .filter(Boolean);

  return (
    <div
      className="card flex flex-col justify-between p-5 transition-all duration-300 hover:-translate-y-1"
      style={{ boxShadow: "var(--shadow)" }}
      onMouseEnter={(e) => (e.currentTarget.style.boxShadow = "var(--shadow-hover)")}
      onMouseLeave={(e) => (e.currentTarget.style.boxShadow = "var(--shadow)")}
    >
      <div>
        <div className="mb-3 flex items-start justify-between">
          <span className={`badge ${meta.cls}`}>
            <i className={`ti ${meta.icon}`}></i> {meta.label}
          </span>
          <span className="text-lg font-bold text-rouge">
            {formatPrice(desk.pricePerHour)}
            <span className="text-xs font-normal text-muted">/h</span>
          </span>
        </div>

        <h3 className="mb-2 text-[15px] font-semibold text-texte">{desk.label}</h3>

        {amenities.length > 0 && (
          <ul className="mb-4 flex flex-wrap gap-1.5">
            {amenities.map((a) => (
              <li
                key={a}
                className="rounded-full bg-gris px-2.5 py-1 text-[11px] text-gray-600"
              >
                {a}
              </li>
            ))}
          </ul>
        )}
      </div>

      <button onClick={() => onReserve(desk)} className="btn-primary w-full justify-center">
        <i className="ti ti-calendar-plus"></i> Réserver
      </button>
    </div>
  );
}
