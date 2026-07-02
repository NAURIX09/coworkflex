export default function StatusBadge({ status }) {
  const map = {
    CONFIRMED: { cls: "badge-green", icon: "ti-circle-check", label: "Confirmée" },
    CANCELLED: { cls: "badge-grey", icon: "ti-circle-x", label: "Annulée" },
  };
  const { cls, icon, label } = map[status] || map.CONFIRMED;
  return (
    <span className={`badge ${cls}`}>
      <i className={`ti ${icon}`}></i>
      {label}
    </span>
  );
}
