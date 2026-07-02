import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useSpaceDetail } from "../hooks/useSpaces.js";
import DeskCard from "../components/DeskCard.jsx";
import ReservationModal from "../components/ReservationModal.jsx";
import Loader from "../components/Loader.jsx";

export default function SpaceDetail() {
  const { id } = useParams();
  const { space, desks, loading, error, refetch } = useSpaceDetail(id);
  const [selectedDesk, setSelectedDesk] = useState(null);

  return (
    <div className="px-5 py-10 md:px-12">
      <Link to="/" className="mb-6 inline-flex items-center gap-1.5 text-sm font-medium text-muted hover:text-rouge">
        <i className="ti ti-arrow-left"></i> Retour aux espaces
      </Link>

      {loading && <Loader label="Chargement de l'espace..." />}

      {error && (
        <div className="flex items-center gap-2 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">
          <i className="ti ti-alert-circle"></i> {error}
        </div>
      )}

      {!loading && !error && (
        <>
          <div
            className="mb-8 flex flex-col gap-4 rounded-card border border-border bg-white p-6 md:flex-row md:items-center md:justify-between"
            style={{ boxShadow: "var(--shadow)" }}
          >
            <div>
              <h1 className="section-title !text-[28px]">
                <i className="ti ti-building-skyscraper"></i> {space?.name || "Espace"}
              </h1>
              <p className="mt-1 flex items-center gap-1.5 text-sm text-muted">
                <i className="ti ti-map-pin-filled"></i> {space?.address}, {space?.city}
              </p>
            </div>
            {typeof space?.capacity === "number" && (
              <span className="badge badge-blue w-fit">
                <i className="ti ti-users"></i> Capacité : {space.capacity} pers.
              </span>
            )}
          </div>

          <h2 className="mb-4 text-lg font-semibold text-texte">
            Postes disponibles ({desks.length})
          </h2>

          {desks.length === 0 ? (
            <div className="flex flex-col items-center gap-2 py-16 text-center text-muted">
              <i className="ti ti-armchair-off text-4xl"></i>
              <p>Aucun poste enregistré pour cet espace pour le moment.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
              {desks.map((desk) => (
                <DeskCard key={desk.id} desk={desk} onReserve={setSelectedDesk} />
              ))}
            </div>
          )}
        </>
      )}

      {selectedDesk && (
        <ReservationModal
          desk={selectedDesk}
          onClose={() => setSelectedDesk(null)}
          onSuccess={refetch}
        />
      )}
    </div>
  );
}
