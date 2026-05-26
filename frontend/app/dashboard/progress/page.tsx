"use client";

import { useQuery } from "@tanstack/react-query";
import Link from "next/link";
import { getProgress, getRoadmap } from "@/lib/api/services";

export default function ProgressPage() {
  const { data: progress, isLoading: loadingProgress } = useQuery({
    queryKey: ["progress"],
    queryFn: getProgress,
  });

  const { data: roadmap, isLoading: loadingRoadmap } = useQuery({
    queryKey: ["roadmap"],
    queryFn: getRoadmap,
  });

  const isLoading = loadingProgress || loadingRoadmap;

  if (isLoading) {
    return (
      <div className="max-w-3xl mx-auto space-y-4 animate-pulse">
        <div className="h-8 bg-gray-800 rounded w-1/3" />
        <div className="h-32 bg-gray-800 rounded-xl" />
        {[1, 2, 3].map((i) => (
          <div key={i} className="h-20 bg-gray-800 rounded-xl" />
        ))}
      </div>
    );
  }

  if (!roadmap || !progress) {
    return (
      <div className="max-w-3xl mx-auto text-center py-16">
        <p className="text-gray-400 mb-4">No roadmap data yet.</p>
        <Link href="/dashboard/profile" className="text-blue-400 hover:underline text-sm">
          Set up your profile to get started →
        </Link>
      </div>
    );
  }

  const completedIds = new Set(progress.completed_task_ids);
  const pct = progress.percentage;

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-semibold text-white">Progress tracker</h1>
        <p className="text-gray-400 text-sm mt-1">
          {progress.completed_tasks} of {progress.total_tasks} tasks completed across {roadmap.phases.length} phases
        </p>
      </div>

      {/* Big overall card */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6">
        <div className="flex items-center justify-between mb-4">
          <span className="text-sm font-medium text-white">Overall completion</span>
          <span className={`text-2xl font-bold ${
            pct >= 75 ? "text-green-400" : pct >= 50 ? "text-blue-400" : "text-amber-400"
          }`}>{pct}%</span>
        </div>

        {/* Segmented progress bar — one segment per phase */}
        <div className="flex gap-1 h-4 rounded-full overflow-hidden">
          {roadmap.phases.map((phase) => {
            const total = phase.tasks.length;
            const done = phase.tasks.filter((t) => completedIds.has(t.id)).length;
            const phasePct = total > 0 ? done / total : 0;
            const segWidth = total / progress.total_tasks;
            return (
              <div
                key={phase.phase_number}
                className="relative bg-gray-800 rounded-sm overflow-hidden"
                style={{ flex: segWidth }}
                title={`Phase ${phase.phase_number}: ${done}/${total}`}
              >
                <div
                  className={`absolute left-0 top-0 h-full transition-all duration-700 ${
                    phasePct === 1 ? "bg-green-500" :
                    phasePct >= 0.5 ? "bg-blue-500" :
                    phasePct > 0 ? "bg-amber-500" : "bg-gray-700"
                  }`}
                  style={{ width: `${phasePct * 100}%` }}
                />
              </div>
            );
          })}
        </div>

        {/* Phase legend */}
        <div className="flex flex-wrap gap-3 mt-3">
          {roadmap.phases.map((phase) => {
            const done = phase.tasks.filter((t) => completedIds.has(t.id)).length;
            const total = phase.tasks.length;
            const complete = done === total;
            return (
              <div key={phase.phase_number} className="flex items-center gap-1.5">
                <div className={`w-2 h-2 rounded-full ${
                  complete ? "bg-green-500" : done > 0 ? "bg-blue-500" : "bg-gray-700"
                }`} />
                <span className="text-xs text-gray-500">
                  P{phase.phase_number} {done}/{total}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Phase breakdown */}
      <div className="space-y-3">
        <h2 className="text-sm font-medium text-white">Phase breakdown</h2>
        {roadmap.phases.map((phase) => {
          const total = phase.tasks.length;
          const done = phase.tasks.filter((t) => completedIds.has(t.id)).length;
          const phasePct = total > 0 ? Math.round((done / total) * 100) : 0;
          const isComplete = done === total;

          return (
            <div
              key={phase.phase_number}
              className={`bg-gray-900 border rounded-xl p-5 ${
                isComplete ? "border-green-500/20" : "border-gray-800"
              }`}
            >
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className={`w-7 h-7 rounded-lg flex items-center justify-center text-xs font-bold border ${
                    isComplete
                      ? "bg-green-500/10 text-green-400 border-green-500/20"
                      : done > 0
                      ? "bg-blue-500/10 text-blue-400 border-blue-500/20"
                      : "bg-gray-800 text-gray-500 border-gray-700"
                  }`}>
                    {isComplete ? "✓" : phase.phase_number}
                  </div>
                  <div>
                    <p className="text-sm font-medium text-white">{phase.title}</p>
                    <p className="text-xs text-gray-600">{total} tasks</p>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-sm font-semibold text-gray-300">
                    {done}/{total}
                  </span>
                  <span className={`text-xs px-2 py-0.5 rounded-full ${
                    isComplete
                      ? "bg-green-500/10 text-green-400"
                      : done > 0
                      ? "bg-blue-500/10 text-blue-400"
                      : "bg-gray-800 text-gray-600"
                  }`}>
                    {phasePct}%
                  </span>
                </div>
              </div>

              <div className="h-1.5 bg-gray-800 rounded-full overflow-hidden">
                <div
                  className={`h-full rounded-full transition-all duration-500 ${
                    isComplete ? "bg-green-500" : done > 0 ? "bg-blue-500" : "bg-gray-700"
                  }`}
                  style={{ width: `${phasePct}%` }}
                />
              </div>

              {/* Completed task chips */}
              {done > 0 && (
                <div className="flex flex-wrap gap-1.5 mt-3">
                  {phase.tasks
                    .filter((t) => completedIds.has(t.id))
                    .slice(0, 5)
                    .map((t) => (
                      <span
                        key={t.id}
                        className="text-xs bg-green-500/10 text-green-500/80 border border-green-500/15 px-2 py-0.5 rounded-full"
                      >
                        ✓ {t.title.length > 24 ? t.title.slice(0, 24) + "…" : t.title}
                      </span>
                    ))}
                  {done > 5 && (
                    <span className="text-xs text-gray-600">+{done - 5} more</span>
                  )}
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* CTA */}
      <Link
        href="/dashboard/roadmap"
        className="flex items-center justify-center gap-2 bg-blue-600/10 hover:bg-blue-600/20 border border-blue-500/20 text-blue-400 text-sm font-medium px-4 py-3 rounded-xl transition"
      >
        Continue roadmap →
      </Link>
    </div>
  );
}
