"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { getAllInterviews, generateInterview, getProfile } from "@/lib/api/services";

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function InterviewPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [customRole, setCustomRole] = useState("");
  const [useCustomRole, setUseCustomRole] = useState(false);
  const [genError, setGenError] = useState("");

  const { data: profile } = useQuery({
    queryKey: ["profile"],
    queryFn: getProfile,
  });

  const { data: sessions, isLoading } = useQuery({
    queryKey: ["interviews"],
    queryFn: getAllInterviews,
  });

  const { mutate: generate, isPending } = useMutation({
    mutationFn: (role: string) => generateInterview(role),
    onSuccess: (session) => {
      queryClient.invalidateQueries({ queryKey: ["interviews"] });
      router.push(`/interview/${session.id}`);
    },
    onError: (err: unknown) => {
      const msg =
        (err as { response?: { data?: { detail?: string } } })?.response?.data
          ?.detail ?? "Failed to generate interview. Please try again.";
      setGenError(msg);
    },
  });

  const handleGenerate = () => {
    setGenError("");
    const role =
      useCustomRole && customRole.trim()
        ? customRole.trim()
        : profile?.target_role ?? "";

    if (!role) {
      setGenError("Please set a target role in your profile first.");
      return;
    }
    generate(role);
  };

  const defaultRole = profile?.target_role ?? "Software Development Engineer";

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-semibold text-white">AI Interview</h1>
        <p className="text-gray-400 text-sm mt-1">
          Gemini generates questions from your resume + role. Practice, get
          feedback, improve.
        </p>
      </div>

      {/* Generate card */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 space-y-4">
        <h2 className="text-sm font-medium text-white">Start a new session</h2>

        {/* Role selector */}
        <div className="space-y-3">
          <label className="flex items-center gap-3 cursor-pointer group">
            <input
              type="radio"
              checked={!useCustomRole}
              onChange={() => setUseCustomRole(false)}
              className="accent-blue-500"
            />
            <span className="text-sm text-gray-300 group-hover:text-white transition">
              Use profile role:{" "}
              <span className="text-blue-400 font-medium">{defaultRole}</span>
            </span>
          </label>

          <label className="flex items-center gap-3 cursor-pointer group">
            <input
              type="radio"
              checked={useCustomRole}
              onChange={() => setUseCustomRole(true)}
              className="accent-blue-500"
            />
            <span className="text-sm text-gray-300 group-hover:text-white transition">
              Use a custom role
            </span>
          </label>

          {useCustomRole && (
            <input
              value={customRole}
              onChange={(e) => setCustomRole(e.target.value)}
              placeholder="e.g. Machine Learning Engineer"
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:border-blue-500 transition ml-6 placeholder:text-gray-600"
            />
          )}
        </div>

        {/* Error */}
        {genError && (
          <div className="bg-red-500/10 border border-red-500/20 text-red-400 text-sm rounded-lg px-4 py-2.5">
            {genError}
          </div>
        )}

        {/* No profile warning */}
        {!profile && (
          <div className="bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs rounded-lg px-4 py-2.5">
            Set up your{" "}
            <Link href="/profile" className="underline underline-offset-2">
              profile
            </Link>{" "}
            first for the best AI questions.
          </div>
        )}

        {/* Generate button */}
        <button
          onClick={handleGenerate}
          disabled={isPending}
          className="w-full bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium rounded-lg py-3 text-sm transition flex items-center justify-center gap-2"
        >
          {isPending ? (
            <>
              <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              Gemini is generating your questions...
            </>
          ) : (
            "Generate AI interview →"
          )}
        </button>

        {isPending && (
          <p className="text-xs text-gray-500 text-center">
            This usually takes 10–20 seconds. Hang tight!
          </p>
        )}
      </div>

      {/* Session history */}
      <div>
        <h2 className="text-sm font-medium text-white mb-3">
          Past sessions
        </h2>

        {isLoading ? (
          <div className="space-y-3 animate-pulse">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-16 bg-gray-800 rounded-xl" />
            ))}
          </div>
        ) : !sessions || sessions.length === 0 ? (
          <div className="bg-gray-900/50 border border-gray-800 rounded-xl p-8 text-center">
            <p className="text-gray-500 text-sm">
              No interviews yet. Start your first session above!
            </p>
          </div>
        ) : (
          <div className="space-y-2">
            {sessions.map((session) => (
              <Link
                key={session.id}
                href={
                  session.submitted
                    ? `/interview/${session.id}/feedback`
                    : `/interview/${session.id}`
                }
                className="flex items-center justify-between bg-gray-900 border border-gray-800 hover:border-gray-700 rounded-xl p-4 transition group"
              >
                <div className="min-w-0">
                  <p className="text-sm font-medium text-gray-200 group-hover:text-white transition truncate">
                    {session.role}
                  </p>
                  <p className="text-xs text-gray-600 mt-0.5">
                    {formatDate(session.created_at)} ·{" "}
                    {session.questions.length} questions
                  </p>
                </div>
                <div className="flex items-center gap-3 ml-3 flex-shrink-0">
                  {session.submitted ? (
                    <span className="text-xs bg-green-500/10 border border-green-500/20 text-green-400 px-2.5 py-1 rounded-full">
                      ✓ Completed
                    </span>
                  ) : (
                    <span className="text-xs bg-amber-500/10 border border-amber-500/20 text-amber-400 px-2.5 py-1 rounded-full">
                      Resume →
                    </span>
                  )}
                  <svg
                    className="w-4 h-4 text-gray-600 group-hover:text-gray-400 transition"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth={2}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M9 5l7 7-7 7"
                    />
                  </svg>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
