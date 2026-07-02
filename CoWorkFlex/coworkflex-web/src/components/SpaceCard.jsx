import { Link } from "react-router-dom";

// Petite palette déterministe pour varier le fond des cartes sans dépendre
// d'images réelles (cohérent avec le style iconographique de ThetDesign).
const PALETTE = [
  { bg: "#fff0f4", icon: "#E8194B" },
  { bg: "#eef2ff", icon: "#4f46e5" },
  { bg: "#f0fdf4", icon: "#16a34a" },
  { bg: "#fff7ed", icon: "#ea580c" },
];

function paletteFor(id) {
  return PALETTE[id % PALETTE.length];
}

export default function SpaceCard({ space }) {
  const { bg, icon } = paletteFor(space.id ?? 0);

  return (
    <Link
      to={`/spaces/${space.id}`}
      className="card group block overflow-hidden transition-all duration-300 hover:-translate-y-1.5"
      style={{ boxShadow: "var(--shadow)" }}
      onMouseEnter={(e) => (e.currentTarget.style.boxShadow = "var(--shadow-hover)")}
      onMouseLeave={(e) => (e.currentTarget.style.boxShadow = "var(--shadow)")}
    >
      <div
        className="relative flex aspect-[4/3] items-center justify-center overflow-hidden"
        style={{ background: bg }}
      >
        <i className="ti ti-building-skyscraper" style={{ fontSize: 64, color: icon }}></i>

        <span className="badge badge-red absolute left-3 top-3">
          <i className="ti ti-map-pin"></i> {space.city}
        </span>

        {typeof space.capacity === "number" && (
          <span className="absolute right-3 top-3 flex items-center gap-1 rounded-full bg-white px-2.5 py-1 text-[11px] font-semibold text-gray-700 shadow">
            <i className="ti ti-users"></i> {space.capacity}
          </span>
        )}
      </div>

      <div className="p-4">
        <div className="mb-1 truncate text-[15px] font-semibold text-texte">
          {space.name}
        </div>
        <div className="mb-3 flex items-center gap-1 text-[13px] text-muted">
          <i className="ti ti-map-pin-filled text-[13px]"></i>
          <span className="truncate">{space.address}</span>
        </div>

        <span className="btn-outline w-full justify-center !py-2 text-[13px] group-hover:border-rouge group-hover:text-rouge">
          Voir les postes <i className="ti ti-arrow-right"></i>
        </span>
      </div>
    </Link>
  );
}
