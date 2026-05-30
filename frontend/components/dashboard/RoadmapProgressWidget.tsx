"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { getRoadmap } from "@/lib/api/services";
import { getProgress } from "@/lib/api/services";

export default function RoadmapProgressWidget() {
  const { data: roadmap, isLoading: loadingRoadmap } = useQuery({
    queryKey: ["roadmap"],
    queryFn: getRoadmap,
  });

  const { data: progress, isLoading: loadingProgress } = useQuery({
    queryKey: ["progress"],
    queryFn: getProgress,
  });

  if (loadingRoadmap || loadingProgress) {
    return (
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
        <div className="animate-pulse space-y-3">
          <div className="h-4 bg-gray-800 rounded w-1/3" />
          <div className="h-2 bg-gray-800 rounded" />
          <div className="h-2 bg-gray-800 rounded" />
          <div className="h-2 bg-gray-800 rounded" />
        </div>
      </div>
    );
  }

  if (!roadmap) {
    return (
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
        <p className="text-gray-500 text-sm">
          No roadmap yet.{" "}
          <Link href="/dashboard/profile" className="text-blue-400 hover:underline">
            Set up your profile
          </Link>{" "}
          to generate one.
        </p>
      </div>
    );
  }

  const completedIds = new Set(progress?.completed_task_ids ?? []);

  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-white">Roadmap progress</h3>
        <Link
          href="/roadmap"
          className="text-xs text-blue-400 hover:text-blue-300 transition"
        >
          View all →
        </Link>
      </div>

      <div className="space-y-4">
        {roadmap.phases.map((phase) => {
          const total = phase.tasks.length;
          const done = phase.tasks.filter((t) =>
            completedIds.has(t.id)
          ).length;
          const pct = total > 0 ? Math.round((done / total) * 100) : 0;

          const barColor =
            pct === 100
              ? "bg-green-500"
              : pct >= 50
              ? "bg-blue-500"
              : "bg-amber-500";

          return (
            <div key={phase.phase_number}>
              <div className="flex justify-between items-center mb-1.5">
                <span className="text-xs text-gray-400 truncate max-w-[200px]">
                  Phase {phase.phase_number}: {phase.title}
                </span>
                <span className="text-xs text-gray-500 ml-2 flex-shrink-0">
                  {done}/{total}
                </span>
              </div>
              <div className="h-1.5 bg-gray-800 rounded-full overflow-hidden">
                <div
                  className={`h-full ${barColor} rounded-full transition-all duration-700`}
                  style={{ width: `${pct}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
