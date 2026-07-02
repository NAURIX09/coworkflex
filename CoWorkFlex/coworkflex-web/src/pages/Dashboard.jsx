import { useState } from "react";
import { useSpaces } from "../hooks/useSpaces.js";
import SpaceCard from "../components/SpaceCard.jsx";
import Loader from "../components/Loader.jsx";

export default function Dashboard() {
  const [cityInput, setCityInput] = useState("");
  const [capacityInput, setCapacityInput] = useState("");
  const [filters, setFilters] = useState({ city: "", capacity: "" });

  const { spaces, loading, error } = useSpaces(filters);

  function handleSearch(e) {
    e.preventDefault();
    setFilters({ city: cityInput.trim(), capacity: capacityInput });
  }

  function resetFilters() {
    setCityInput("");
    setCapacityInput("");
    setFilters({ city: "", capacity: "" });
  }

  return (
    <div className="px-5 py-10 md:px-12">
      <div className="mb-8">
        <h1 className="section-title">
          <i className="ti ti-building-skyscraper"></i> Espaces de coworking
        </h1>
        <p className="mt-1 text-sm text-muted">
          Trouvez et réservez un poste de travail, une salle de réunion ou un bureau privé.
        </p>
      </div>

      <form
        onSubmit={handleSearch}
        className="mb-8 flex flex-col gap-3 rounded-card border border-border bg-white p-4 md:flex-row md:items-end"
        style={{ boxShadow: "var(--shadow)" }}
      >
        <div className="flex-1">
          <label className="mb-1.5 block text-[13px] font-medium text-gray-700">Ville</label>
          <div className="flex items-center gap-2 rounded-[10px] border-[1.5px] border-border px-3.5 py-2.5">
            <i className="ti ti-map-pin text-gray-400"></i>
            <input
              value={cityInput}
              onChange={(e) => setCityInput(e.target.value)}
              placeholder="Abidjan, Bouaké..."
              className="w-full border-none bg-transparent text-sm outline-none"
            />
          </div>
        </div>

        <div className="w-full md:w-48">
          <label className="mb-1.5 block text-[13px] font-medium text-gray-700">
            Capacité min.
          </label>
          <div className="flex items-center gap-2 rounded-[10px] border-[1.5px] border-border px-3.5 py-2.5">
            <i className="ti ti-users text-gray-400"></i>
            <input
              type="number"
              min="0"
              value={capacityInput}
              onChange={(e) => setCapacityInput(e.target.value)}
              placeholder="Ex: 10"
              className="w-full border-none bg-transparent text-sm outline-none"
            />
          </div>
        </div>

        <div className="flex gap-2">
          <button type="submit" className="btn-primary">
            <i className="ti ti-search"></i> Rechercher
          </button>
          {(filters.city || filters.capacity) && (
            <button type="button" onClick={resetFilters} className="btn-outline">
              Réinitialiser
            </button>
          )}
        </div>
      </form>

      {loading && <Loader label="Chargement des espaces..." />}

      {error && (
        <div className="flex items-center gap-2 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">
          <i className="ti ti-alert-circle"></i> {error}
        </div>
      )}

      {!loading && !error && spaces.length === 0 && (
        <div className="flex flex-col items-center gap-2 py-20 text-center text-muted">
          <i className="ti ti-building-off text-4xl"></i>
          <p>Aucun espace ne correspond à votre recherche.</p>
        </div>
      )}

      {!loading && !error && spaces.length > 0 && (
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {spaces.map((space) => (
            <SpaceCard key={space.id} space={space} />
          ))}
        </div>
      )}
    </div>
  );
}
