export default function Loader({ label = "Chargement..." }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-muted">
      <i className="ti ti-loader-2 spin text-3xl text-rouge"></i>
      <span className="text-sm">{label}</span>
    </div>
  );
}
