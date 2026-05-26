"use client";

import { useState } from "react";
import TaskCheckbox from "./TaskCheckbox";
import type { Phase } from "@/types";

interface RoadmapPhaseProps {
  phase: Phase;
  completedIds: Set<string>;
  onToggleTask: (taskId: string, completed: boolean) => void;
  pendingTaskId?: string | null;
  defaultOpen?: boolean;
}

export default function RoadmapPhase({
  phase,
  completedIds,
  onToggleTask,
  pendingTaskId,
  defaultOpen = false,
}: RoadmapPhaseProps) {
  const [isOpen, setIsOpen] = useState(defaultOpen);

  const total = phase.tasks.length;
  const done = phase.tasks.filter((t) => completedIds.has(t.id)).length;
  const pct = total > 0 ? Math.round((done / total) * 100) : 0;
  const isComplete = done === total;

  const barColor = isComplete
    ? "bg-green-500"
    : pct >= 50
    ? "bg-blue-500"
    : pct > 0
    ? "bg-amber-500"
    : "bg-gray-700";

  const badgeColor = isComplete
    ? "text-green-400 bg-green-500/10 border-green-500/20"
    : pct > 0
    ? "text-blue-400 bg-blue-500/10 border-blue-500/20"
    : "text-gray-500 bg-gray-800 border-gray-700";

  return (
    <div
      className={`border rounded-xl transition-all overflow-hidden ${
        isComplete ? "border-green-500/20" : "border-gray-800"
      }`}
    >
      {/* Phase header */}
      <button
        onClick={() => setIsOpen((o) => !o)}
        className="w-full text-left p-5 flex items-start gap-4"
      >
        {/* Phase number badge */}
        <div
          className={`w-8 h-8 rounded-lg flex items-center justify-center text-xs font-bold flex-shrink-0 border ${badgeColor}`}
        >
          {isComplete ? "✓" : phase.phase_number}
        </div>

        {/* Title + progress */}
        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-sm font-semibold text-white">
              {phase.title}
            </h3>
            <div className="flex items-center gap-3 ml-3 flex-shrink-0">
              <span className="text-xs text-gray-500">
                {done}/{total} tasks
              </span>
              <span className="text-xs font-medium text-gray-400">
                {pct}%
              </span>
              <svg
                className={`w-4 h-4 text-gray-600 transition-transform ${
                  isOpen ? "rotate-180" : ""
                }`}
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth={2}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M19 9l-7 7-7-7"
                />
              </svg>
            </div>
          </div>

          {/* Progress bar */}
          <div className="h-1.5 bg-gray-800 rounded-full overflow-hidden">
            <div
              className={`h-full ${barColor} rounded-full transition-all duration-500`}
              style={{ width: `${pct}%` }}
            />
          </div>

          {/* Description — only when collapsed */}
          {!isOpen && (
            <p className="text-xs text-gray-600 mt-2 line-clamp-1">
              {phase.description}
            </p>
          )}
        </div>
      </button>

      {/* Task list — expanded */}
      {isOpen && (
        <div className="px-5 pb-5 space-y-2">
          {phase.description && (
            <p className="text-xs text-gray-500 mb-3 leading-relaxed">
              {phase.description}
            </p>
          )}

          {phase.tasks.map((task) => (
            <TaskCheckbox
              key={task.id}
              task={task}
              checked={completedIds.has(task.id)}
              onChange={onToggleTask}
              disabled={pendingTaskId === task.id}
            />
          ))}
        </div>
      )}
    </div>
  );
}
