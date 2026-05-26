interface ReadinessScoreProps {
  score: number; // 0–100
}

export default function ReadinessScore({ score }: ReadinessScoreProps) {
  const radius = 54;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;

  const color =
    score >= 75
      ? "#22c55e" // green
      : score >= 50
      ? "#3b82f6" // blue
      : "#f59e0b"; // amber

  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-5 flex flex-col items-center justify-center">
      <p className="text-xs text-gray-500 uppercase tracking-wider mb-4">
        Readiness Score
      </p>

      <div className="relative w-32 h-32">
        <svg
          className="rotate-[-90deg]"
          width="128"
          height="128"
          viewBox="0 0 128 128"
        >
          {/* Background ring */}
          <circle
            cx="64"
            cy="64"
            r={radius}
            fill="none"
            stroke="#1f2937"
            strokeWidth="10"
          />
          {/* Score ring */}
          <circle
            cx="64"
            cy="64"
            r={radius}
            fill="none"
            stroke={color}
            strokeWidth="10"
            strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={offset}
            style={{ transition: "stroke-dashoffset 1s ease" }}
          />
        </svg>
        {/* Score text */}
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-2xl font-bold text-white">{score}%</span>
        </div>
      </div>

      <p className="text-xs text-gray-500 mt-4 text-center">
        {score >= 75
          ? "You're interview ready! 🎉"
          : score >= 50
          ? "Keep going, almost there"
          : "Just getting started — let's go!"}
      </p>
    </div>
  );
}
