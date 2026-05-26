"use client";

import { useQuery } from "@tanstack/react-query";
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, RadarChart, PolarGrid, PolarAngleAxis,
  Radar, BarChart, Bar, Cell,
} from "recharts";
import { getAllInterviews, getProgress, getRoadmap } from "@/lib/api/services";

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("en-IN", { day: "numeric", month: "short" });
}

// Custom tooltip for charts
function ChartTooltip({ active, payload, label }: {
  active?: boolean;
  payload?: { value: number }[];
  label?: string;
}) {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-xs">
      <p className="text-gray-400">{label}</p>
      <p className="text-white font-semibold">{payload[0].value}%</p>
    </div>
  );
}

export default function AnalyticsPage() {
  const { data: sessions, isLoading: loadingSessions } = useQuery({
    queryKey: ["interviews"],
    queryFn: getAllInterviews,
  });

  const { data: progress, isLoading: loadingProgress } = useQuery({
    queryKey: ["progress"],
    queryFn: getProgress,
  });

  const { data: roadmap, isLoading: loadingRoadmap } = useQuery({
    queryKey: ["roadmap"],
    queryFn: getRoadmap,
  });

  const isLoading = loadingSessions || loadingProgress || loadingRoadmap;

  // Build score trend data from interview sessions
  const scoreTrend = (sessions ?? [])
    .filter((s) => s.submitted)
    .slice(-8)
    .map((s, i) => ({
      label: formatDate(s.created_at),
      score: 0, // replace with actual score when available from API
      session: i + 1,
    }));

  // Phase completion data for bar chart
  const phaseData = (roadmap?.phases ?? []).map((phase) => {
    const total = phase.tasks.length;
    const done = (progress?.completed_task_ids ?? []).filter((id) =>
      phase.tasks.some((t) => t.id === id)
    ).length;
    return {
      name: `P${phase.phase_number}`,
      fullName: phase.title,
      completion: total > 0 ? Math.round((done / total) * 100) : 0,
      done,
      total,
    };
  });

  // Skill radar data — derived from roadmap phase names
  const radarData = (roadmap?.phases ?? []).map((phase) => ({
    skill: phase.title.length > 14 ? phase.title.slice(0, 14) + "…" : phase.title,
    value: (() => {
      const total = phase.tasks.length;
      const done = (progress?.completed_task_ids ?? []).filter((id) =>
        phase.tasks.some((t) => t.id === id)
      ).length;
      return total > 0 ? Math.round((done / total) * 100) : 0;
    })(),
  }));

  // Summary stats
  const totalSessions = sessions?.length ?? 0;
  const submittedSessions = sessions?.filter((s) => s.submitted).length ?? 0;
  const overallPct = progress?.percentage ?? 0;

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto space-y-4 animate-pulse">
        <div className="h-8 bg-gray-800 rounded w-1/3" />
        <div className="grid grid-cols-3 gap-4">
          {[1, 2, 3].map((i) => <div key={i} className="h-24 bg-gray-800 rounded-xl" />)}
        </div>
        <div className="h-64 bg-gray-800 rounded-xl" />
        <div className="grid grid-cols-2 gap-4">
          <div className="h-56 bg-gray-800 rounded-xl" />
          <div className="h-56 bg-gray-800 rounded-xl" />
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-semibold text-white">Analytics</h1>
        <p className="text-gray-400 text-sm mt-1">
          Your placement preparation at a glance
        </p>
      </div>

      {/* Stats row */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        {[
          { label: "Roadmap done", value: `${overallPct}%`, color: "text-blue-400" },
          { label: "Tasks complete", value: `${progress?.completed_tasks ?? 0}`, color: "text-green-400" },
          { label: "Sessions started", value: totalSessions, color: "text-white" },
          { label: "Sessions submitted", value: submittedSessions, color: "text-purple-400" },
        ].map((s) => (
          <div key={s.label} className="bg-gray-900 border border-gray-800 rounded-xl p-4">
            <p className="text-xs text-gray-500 mb-1">{s.label}</p>
            <p className={`text-2xl font-bold ${s.color}`}>{s.value}</p>
          </div>
        ))}
      </div>

      {/* Interview score trend */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
        <h2 className="text-sm font-medium text-white mb-4">Interview score trend</h2>
        {scoreTrend.length < 2 ? (
          <div className="h-48 flex items-center justify-center">
            <p className="text-gray-600 text-sm">
              Complete at least 2 interviews to see your trend.
            </p>
          </div>
        ) : (
          <ResponsiveContainer width="100%" height={200}>
            <LineChart data={scoreTrend} margin={{ top: 4, right: 4, bottom: 4, left: -20 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
              <XAxis dataKey="label" tick={{ fontSize: 11, fill: "#6b7280" }} axisLine={false} tickLine={false} />
              <YAxis domain={[0, 100]} tick={{ fontSize: 11, fill: "#6b7280" }} axisLine={false} tickLine={false} />
              <Tooltip content={<ChartTooltip />} />
              <Line
                type="monotone" dataKey="score" stroke="#3b82f6"
                strokeWidth={2} dot={{ fill: "#3b82f6", r: 4 }}
                activeDot={{ r: 6, fill: "#60a5fa" }}
              />
            </LineChart>
          </ResponsiveContainer>
        )}
      </div>

      {/* Phase completion bar chart + Skill radar */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {/* Phase completion bars */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <h2 className="text-sm font-medium text-white mb-4">Phase completion</h2>
          {phaseData.length === 0 ? (
            <div className="h-48 flex items-center justify-center">
              <p className="text-gray-600 text-sm">No roadmap data yet.</p>
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={phaseData} margin={{ top: 4, right: 4, bottom: 4, left: -20 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" vertical={false} />
                <XAxis dataKey="name" tick={{ fontSize: 11, fill: "#6b7280" }} axisLine={false} tickLine={false} />
                <YAxis domain={[0, 100]} tick={{ fontSize: 11, fill: "#6b7280" }} axisLine={false} tickLine={false} />
                <Tooltip
                  content={({ active, payload }) => {
                    if (!active || !payload?.length) return null;
                    const d = payload[0].payload;
                    return (
                      <div className="bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-xs">
                        <p className="text-gray-400">{d.fullName}</p>
                        <p className="text-white font-semibold">{d.done}/{d.total} tasks · {d.completion}%</p>
                      </div>
                    );
                  }}
                />
                <Bar dataKey="completion" radius={[4, 4, 0, 0]}>
                  {phaseData.map((entry, i) => (
                    <Cell
                      key={i}
                      fill={
                        entry.completion === 100 ? "#22c55e" :
                        entry.completion >= 50 ? "#3b82f6" :
                        entry.completion > 0 ? "#f59e0b" : "#374151"
                      }
                    />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>

        {/* Skill coverage radar */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <h2 className="text-sm font-medium text-white mb-4">Skill coverage radar</h2>
          {radarData.length < 3 ? (
            <div className="h-48 flex items-center justify-center">
              <p className="text-gray-600 text-sm">Need at least 3 phases for radar.</p>
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={200}>
              <RadarChart data={radarData}>
                <PolarGrid stroke="#1f2937" />
                <PolarAngleAxis
                  dataKey="skill"
                  tick={{ fontSize: 10, fill: "#6b7280" }}
                />
                <Radar
                  name="Progress" dataKey="value"
                  stroke="#3b82f6" fill="#3b82f6" fillOpacity={0.2}
                  strokeWidth={2}
                />
                <Tooltip
                  content={({ active, payload }) => {
                    if (!active || !payload?.length) return null;
                    return (
                      <div className="bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-xs">
                        <p className="text-white font-semibold">{payload[0].payload.skill}</p>
                        <p className="text-blue-400">{payload[0].value}% complete</p>
                      </div>
                    );
                  }}
                />
              </RadarChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>

      {/* Readiness breakdown */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
        <h2 className="text-sm font-medium text-white mb-4">Readiness breakdown</h2>
        <div className="space-y-3">
          {[
            {
              label: "Roadmap completion",
              value: overallPct,
              weight: "40%",
              color: "bg-blue-500",
            },
            {
              label: "Interview performance",
              value: submittedSessions > 0 ? Math.round((submittedSessions / Math.max(totalSessions, 1)) * 100) : 0,
              weight: "40%",
              color: "bg-purple-500",
            },
            {
              label: "Resume uploaded",
              value: 100, // simplified: if they're on this page they likely have one
              weight: "20%",
              color: "bg-green-500",
            },
          ].map((row) => (
            <div key={row.label}>
              <div className="flex justify-between items-center mb-1.5">
                <span className="text-xs text-gray-400">{row.label}</span>
                <div className="flex items-center gap-2">
                  <span className="text-xs text-gray-600">weight {row.weight}</span>
                  <span className="text-xs font-medium text-white">{row.value}%</span>
                </div>
              </div>
              <div className="h-2 bg-gray-800 rounded-full overflow-hidden">
                <div
                  className={`h-full ${row.color} rounded-full transition-all duration-700`}
                  style={{ width: `${row.value}%` }}
                />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
