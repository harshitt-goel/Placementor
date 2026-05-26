"use client";

import { useState } from "react";

interface Task {
  id: string;
  title: string;
  description: string;
  resources?: string[];
}

interface TaskCheckboxProps {
  task: Task;
  checked: boolean;
  onChange: (taskId: string, newChecked: boolean) => void;
  disabled?: boolean;
}

export default function TaskCheckbox({
  task,
  checked,
  onChange,
  disabled,
}: TaskCheckboxProps) {
  const [expanded, setExpanded] = useState(false);

  return (
    <div
      className={`rounded-lg border transition-all ${
        checked
          ? "border-green-500/20 bg-green-500/5"
          : "border-gray-800 bg-gray-800/30 hover:bg-gray-800/50"
      }`}
    >
      <div className="flex items-start gap-3 p-3">
        {/* Checkbox */}
        <button
          onClick={() => !disabled && onChange(task.id, !checked)}
          disabled={disabled}
          className={`mt-0.5 w-5 h-5 rounded flex-shrink-0 border-2 flex items-center justify-center transition-all ${
            checked
              ? "bg-green-500 border-green-500"
              : "border-gray-600 hover:border-blue-400"
          } ${disabled ? "opacity-50 cursor-not-allowed" : "cursor-pointer"}`}
          aria-label={checked ? "Mark incomplete" : "Mark complete"}
        >
          {checked && (
            <svg
              className="w-3 h-3 text-white"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={3}
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M5 13l4 4L19 7"
              />
            </svg>
          )}
        </button>

        {/* Task info */}
        <div className="flex-1 min-w-0">
          <button
            onClick={() => setExpanded((e) => !e)}
            className="text-left w-full"
          >
            <p
              className={`text-sm font-medium transition-colors ${
                checked ? "line-through text-gray-500" : "text-gray-200"
              }`}
            >
              {task.title}
            </p>
          </button>

          {/* Expand: description + resources */}
          {expanded && (
            <div className="mt-2 space-y-2">
              <p className="text-xs text-gray-500 leading-relaxed">
                {task.description}
              </p>
              {task.resources && task.resources.length > 0 && (
                <div>
                  <p className="text-xs text-gray-600 mb-1">Resources:</p>
                  <div className="flex flex-wrap gap-2">
                    {task.resources.map((url, i) => (
                      <a
                        key={i}
                        href={url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-xs text-blue-400 hover:text-blue-300 underline underline-offset-2 transition"
                      >
                        Resource {i + 1} ↗
                      </a>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Expand toggle */}
        <button
          onClick={() => setExpanded((e) => !e)}
          className="text-gray-600 hover:text-gray-400 transition flex-shrink-0 mt-0.5"
          aria-label={expanded ? "Collapse" : "Expand"}
        >
          <svg
            className={`w-4 h-4 transition-transform ${
              expanded ? "rotate-180" : ""
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
        </button>
      </div>
    </div>
  );
}
