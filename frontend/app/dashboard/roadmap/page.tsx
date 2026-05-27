"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import Link from "next/link";
import {
  getRoadmap,
  generateRoadmap,
  getProgress,
  completeTask,
  uncompleteTask,
} from "@/lib/api/services";
import RoadmapPhase from "./RoadmapPhase";
import TaskCheckbox from "./TaskCheckbox";

export default function RoadmapPage() {
  const queryClient = useQueryClient();
  const [pendingTaskId, setPendingTaskId] = useState<string | null>(null);

  // Fetch roadmap structure
  const {
  data: roadmap,
  isLoading: loadingRoadmap,
  error: roadmapError,
  refetch: refetchRoadmap,
} = useQuery({
  queryKey: ["roadmap"],
  queryFn: getRoadmap,
  retry: false,
});

const generateMutation = useMutation({
  mutationFn: generateRoadmap,
  onSuccess: () => {
    refetchRoadmap();
  },
});

  // Fetch completed task IDs
  const { data: progress, isLoading: loadingProgress } = useQuery({
    queryKey: ["progress"],
    queryFn: getProgress,
  });

  // Optimistic task completion toggle
  const { mutate: toggleTask } = useMutation({
    mutationFn: async ({
      taskId,
      completed,
    }: {
      taskId: string;
      completed: boolean;
    }) => {
      if (completed) {
        await completeTask(taskId);
      } else {
        await uncompleteTask(taskId);
      }
    },
    onMutate: async ({ taskId, completed }) => {
      setPendingTaskId(taskId);

      // Cancel any outgoing refetches
      await queryClient.cancelQueries({ queryKey: ["progress"] });

      // Snapshot the previous value for rollback
      const prev = queryClient.getQueryData(["progress"]);

      // Optimistically update the cache
      queryClient.setQueryData(["progress"], (old: typeof progress) => {
        if (!old) return old;
        const ids = new Set(old.completed_task_ids);
        if (completed) {
          ids.add(taskId);
        } else {
          ids.delete(taskId);
        }
        const newCompleted = ids.size;
        return {
          ...old,
          completed_task_ids: Array.from(ids),
          completed_tasks: newCompleted,
          percentage: Math.round((newCompleted / old.total_tasks) * 100),
        };
      });

      return { prev };
    },
    onError: (_err, _vars, context) => {
      // Roll back on error
      if (context?.prev) {
        queryClient.setQueryData(["progress"], context.prev);
      }
    },
    onSettled: () => {
      setPendingTaskId(null);
      queryClient.invalidateQueries({ queryKey: ["progress"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
  });

  const handleToggleTask = (taskId: string, completed: boolean) => {
    toggleTask({ taskId, completed });
  };

  // ── Loading ───────────────────────────────────────────────────────────────
  if (loadingRoadmap || loadingProgress) {
    return (
      <div className="max-w-3xl mx-auto space-y-4 animate-pulse">
        <div className="h-8 bg-gray-800 rounded w-1/3" />
        <div className="h-3 bg-gray-800 rounded w-full" />
        {[1, 2, 3].map((i) => (
          <div key={i} className="h-24 bg-gray-800 rounded-xl" />
        ))}
      </div>
    );
  }

  // ── No roadmap ────────────────────────────────────────────────────────────
  if (roadmapError || !roadmap) {
    return (
      <div className="max-w-3xl mx-auto">
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-10 text-center">
          <div className="text-4xl mb-4">⬡</div>
          <h2 className="text-white font-semibold text-lg mb-2">
            No roadmap yet
          </h2>
          <p className="text-gray-400 text-sm mb-6 max-w-sm mx-auto">
            Set up your profile with your target role and domain, and
            we&apos;ll generate a personalized roadmap for you.
          </p>
          <button
  onClick={() => generateMutation.mutate()}
  className="bg-blue-600 hover:bg-blue-500 text-white text-sm font-medium px-6 py-2.5 rounded-lg transition"
>
  Generate roadmap →
</button>
        
        </div>
      </div>
    );
  }

  // ── Derived state ─────────────────────────────────────────────────────────
  const completedIds = new Set(progress?.completed_task_ids ?? []);
  const totalTasks = progress?.total_tasks ?? 0;
  const completedCount = progress?.completed_tasks ?? 0;
  const overallPct = progress?.percentage ?? 0;

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-semibold text-white">Your roadmap</h1>
        <p className="text-gray-400 text-sm mt-1">
          {roadmap.role} — {roadmap.phases.length} phases · {totalTasks} tasks
        </p>
      </div>

      {/* Overall progress bar */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
        <div className="flex items-center justify-between mb-3">
          <span className="text-sm text-gray-300 font-medium">
            Overall completion
          </span>
          <span className="text-sm font-semibold text-white">
            {completedCount} / {totalTasks} tasks
          </span>
        </div>

        {/* Big progress bar */}
        <div className="h-3 bg-gray-800 rounded-full overflow-hidden">
          <div
            className={`h-full rounded-full transition-all duration-700 ${
              overallPct === 100
                ? "bg-green-500"
                : overallPct >= 50
                ? "bg-blue-500"
                : "bg-amber-500"
            }`}
            style={{ width: `${overallPct}%` }}
          />
        </div>

        <div className="flex items-center justify-between mt-2">
          <span className="text-xs text-gray-600">0%</span>
          <span
            className={`text-sm font-bold ${
              overallPct === 100
                ? "text-green-400"
                : overallPct >= 50
                ? "text-blue-400"
                : "text-amber-400"
            }`}
          >
            {overallPct}%
          </span>
          <span className="text-xs text-gray-600">100%</span>
        </div>

        {/* Milestone labels */}
        <div className="flex gap-4 mt-4 flex-wrap">
          {[
            { pct: 25, label: "Beginner" },
            { pct: 50, label: "Intermediate" },
            { pct: 75, label: "Advanced" },
            { pct: 100, label: "Interview Ready" },
          ].map((m) => (
            <div
              key={m.pct}
              className={`flex items-center gap-1.5 text-xs ${
                overallPct >= m.pct ? "text-gray-300" : "text-gray-600"
              }`}
            >
              <div
                className={`w-2 h-2 rounded-full ${
                  overallPct >= m.pct ? "bg-blue-500" : "bg-gray-700"
                }`}
              />
              {m.label}
            </div>
          ))}
        </div>
      </div>

      {/* Phase list */}
      <div className="space-y-3">
        {roadmap.phases.map((phase, i) => (
          <RoadmapPhase
            key={phase.phase_number}
            phase={phase}
            completedIds={completedIds}
            onToggleTask={handleToggleTask}
            pendingTaskId={pendingTaskId}
            defaultOpen={i === 0}
          />
        ))}
      </div>

      {/* Completion celebration */}
      {overallPct === 100 && (
        <div className="bg-green-500/10 border border-green-500/20 rounded-xl p-6 text-center">
          <p className="text-2xl mb-2">🎉</p>
          <h3 className="text-green-400 font-semibold text-lg">
            Roadmap complete!
          </h3>
          <p className="text-gray-400 text-sm mt-1 mb-4">
            You&apos;ve finished all phases. Time to ace those interviews.
          </p>
          <Link
            href="/interview"
            className="bg-green-500 hover:bg-green-400 text-gray-950 font-semibold text-sm px-6 py-2.5 rounded-lg transition"
          >
            Start final interview practice →
          </Link>
        </div>
      )}
    </div>
  );
}
