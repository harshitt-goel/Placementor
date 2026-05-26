"use client";

import { useQuery } from "@tanstack/react-query";
import Link from "next/link";
import { getDashboard, getProfile } from "@/lib/api/services";
import { useAuthStore } from "@/store/authStore";
import StatsCard from "@/components/dashboard/StatsCard";
import ReadinessScore from "@/components/dashboard/ReadinessScore";
import RoadmapProgressWidget from "@/components/dashboard/RoadmapProgressWidget";
import RecentInterviews from "@/components/dashboard/RecentInterviews";

export default function DashboardPage() {
  const { user } = useAuthStore();

  const { data: dashboard, isLoading: loadingDash } = useQuery({
    queryKey: ["dashboard"],
    queryFn: getDashboard,
  });

  const { data: profile } = useQuery({
    queryKey: ["profile"],
    queryFn: getProfile,
  });

  const readiness = dashboard?.readiness_score ?? 0;
  const roadmapPct = dashboard?.roadmap_percentage ?? 0;
  const totalInterviews = dashboard?.total_interviews ?? 0;
  const avgScore = dashboard?.average_interview_score ?? 0;

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      {/* Greeting */}
      <div>
        <h1 className="text-2xl font-semibold text-white">
          Welcome back, {user?.username ?? "there"} 👋
        </h1>
        <p className="text-gray-400 text-sm mt-1">
          {profile
            ? `Targeting ${profile.target_role} at ${profile.target_company ?? "your dream company"}`
            : "Complete your profile to get started →"}
        </p>
      </div>

      {/* Profile incomplete banner */}
      {!profile && (
        <div className="bg-amber-500/10 border border-amber-500/20 rounded-xl px-5 py-4 flex items-center justify-between">
          <div>
            <p className="text-amber-400 text-sm font-medium">
              Profile not set up
            </p>
            <p className="text-amber-500/70 text-xs mt-0.5">
              Add your target role and domain to generate your roadmap and
              interviews.
            </p>
          </div>
          <Link
            href="/profile"
            className="bg-amber-500 hover:bg-amber-400 text-gray-950 text-xs font-semibold px-4 py-2 rounded-lg transition flex-shrink-0 ml-4"
          >
            Set up profile →
          </Link>
        </div>
      )}

      {/* Top row: Readiness + Stats */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-4">
        {/* Readiness score ring */}
        <div className="lg:col-span-1">
          <ReadinessScore score={readiness} />
        </div>

        {/* Stats cards */}
        <div className="lg:col-span-3 grid grid-cols-1 sm:grid-cols-3 gap-4">
          <StatsCard
            label="Roadmap progress"
            value={`${roadmapPct}%`}
            sub={
              loadingDash
                ? "Loading..."
                : roadmapPct === 100
                ? "✓ Roadmap complete!"
                : `${100 - roadmapPct}% remaining`
            }
            trend={roadmapPct >= 50 ? "up" : "neutral"}
          />
          <StatsCard
            label="Interviews done"
            value={totalInterviews}
            sub={
              totalInterviews > 0
                ? `Avg score: ${Math.round(avgScore)}%`
                : "Start your first interview"
            }
            trend={avgScore >= 70 ? "up" : "neutral"}
          />
          <StatsCard
            label="Target company"
            value={profile?.target_company ?? "—"}
            sub={profile?.target_role ?? "Set your profile"}
            trend="neutral"
          />
        </div>
      </div>

      {/* Middle row: Roadmap widget + Quick actions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2">
          <RoadmapProgressWidget />
        </div>

        {/* Quick actions */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-5">
          <h3 className="text-sm font-medium text-white mb-4">
            Quick actions
          </h3>
          <div className="space-y-2">
            <Link
              href="/interview"
              className="flex items-center gap-3 w-full text-left bg-blue-600/10 hover:bg-blue-600/20 border border-blue-500/20 text-blue-400 px-4 py-3 rounded-lg text-sm transition"
            >
              <span className="text-base">◎</span>
              Start AI interview
            </Link>
            <Link
              href="/resume"
              className="flex items-center gap-3 w-full text-left bg-gray-800/60 hover:bg-gray-800 border border-gray-700/50 text-gray-300 px-4 py-3 rounded-lg text-sm transition"
            >
              <span className="text-base">⬆</span>
              Upload / view resume
            </Link>
            <Link
              href="/roadmap"
              className="flex items-center gap-3 w-full text-left bg-gray-800/60 hover:bg-gray-800 border border-gray-700/50 text-gray-300 px-4 py-3 rounded-lg text-sm transition"
            >
              <span className="text-base">⬡</span>
              View roadmap
            </Link>
            <Link
              href="/profile"
              className="flex items-center gap-3 w-full text-left bg-gray-800/60 hover:bg-gray-800 border border-gray-700/50 text-gray-300 px-4 py-3 rounded-lg text-sm transition"
            >
              <span className="text-base">◯</span>
              Update profile
            </Link>
          </div>
        </div>
      </div>

      {/* Bottom row: Recent interviews */}
      <RecentInterviews />
    </div>
  );
}
