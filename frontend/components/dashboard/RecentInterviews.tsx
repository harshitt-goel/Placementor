"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { getAllInterviews } from "@/lib/api/services";

function ScoreBadge({ score }: { score: number }) {
  const cls =
    score >= 70
      ? "bg-green-500/10 text-green-400 border-green-500/20"
      : score >= 50
      ? "bg-amber-500/10 text-amber-400 border-amber-500/20"
      : "bg-red-500/10 text-red-400 border-red-500/20";

  return (
    <span
      className={`text-xs font-medium px-2.5 py-0.5 rounded-full border ${cls}`}
    >
      {score}%
    </span>
  );
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
  });
}

export default function RecentInterviews() {
  const { data: sessions, isLoading } = useQuery({
    queryKey: ["interviews"],
    queryFn: getAllInterviews,
  });

  if (isLoading) {
    return (
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
        <div className="animate-pulse space-y-3">
          <div className="h-4 bg-gray-800 rounded w-1/4" />
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-10 bg-gray-800 rounded" />
          ))}
        </div>
      </div>
    );
  }

  const recent = sessions?.slice(0, 4) ?? [];

  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-white">Recent interviews</h3>
        <Link
          href="/dashboard/interview"
          className="text-xs text-blue-400 hover:text-blue-300 transition"
        >
          View all →
        </Link>
      </div>

      {recent.length === 0 ? (
        <div className="text-center py-6">
          <p className="text-gray-500 text-sm mb-3">No interviews yet</p>
          <Link
            href="/dashboard/interview"
            className="bg-blue-600 hover:bg-blue-500 text-white text-xs font-medium px-4 py-2 rounded-lg transition"
          >
            Start your first interview
          </Link>
        </div>
      ) : (
        <div className="space-y-1">
          {recent.map((session) => (
            <Link
              key={session.id}
              href={`/dashboard/interview/${session.id}/feedback`}
              className="flex items-center justify-between py-2.5 px-2 rounded-lg hover:bg-gray-800/50 transition group"
            >
              <div className="min-w-0">
                <p className="text-sm text-gray-300 truncate group-hover:text-white transition">
                  {session.role} — Session #{session.id}
                </p>
                <p className="text-xs text-gray-600">
                  {formatDate(session.created_at)}
                </p>
              </div>
              {/* Score shown if submitted */}
              {session.submitted ? (
                <ScoreBadge score={0} /> // replace 0 with actual score from feedback
              ) : (
                <span className="text-xs text-amber-500 border border-amber-500/20 bg-amber-500/10 px-2.5 py-0.5 rounded-full">
                  In progress
                </span>
              )}
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
