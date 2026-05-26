interface StatsCardProps {
  label: string;
  value: string | number;
  sub?: string;
  trend?: "up" | "down" | "neutral";
}

export default function StatsCard({
  label,
  value,
  sub,
  trend,
}: StatsCardProps) {
  const trendColor =
    trend === "up"
      ? "text-green-400"
      : trend === "down"
      ? "text-red-400"
      : "text-gray-500";

  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
      <p className="text-xs text-gray-500 uppercase tracking-wider mb-2">
        {label}
      </p>
      <p className="text-3xl font-semibold text-white">{value}</p>
      {sub && (
        <p className={`text-xs mt-1.5 ${trendColor}`}>{sub}</p>
      )}
    </div>
  );
}
