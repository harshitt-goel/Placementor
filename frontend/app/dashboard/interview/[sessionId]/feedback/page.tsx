"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import Link from "next/link";
import { getInterviewFeedback } from "@/lib/api/services";

function ScoreBadge({ score }: { score: number }) {
  const cls =
    score >= 70
      ? "bg-green-500/15 text-green-400 border-green-500/25"
      : score >= 50
      ? "bg-amber-500/15 text-amber-400 border-amber-500/25"
      : "bg-red-500/15 text-red-400 border-red-500/25";
  return (
    <span className={`text-sm font-bold px-3 py-1 rounded-full border ${cls}`}>
      {score}%
    </span>
  );
}

function ScoreRing({ score }: { score: number }) {
  const r = 44;
  const circ = 2 * Math.PI * r;
  const offset = circ - (score / 100) * circ;
  const color = score >= 70 ? "#22c55e" : score >= 50 ? "#f59e0b" : "#ef4444";
  return (
    <div className="relative w-24 h-24">
      <svg className="rotate-[-90deg]" width="96" height="96" viewBox="0 0 96 96">
        <circle cx="48" cy="48" r={r} fill="none" stroke="#1f2937" strokeWidth="8" />
        <circle
          cx="48" cy="48" r={r} fill="none" stroke={color}
          strokeWidth="8" strokeLinecap="round"
          strokeDasharray={circ} strokeDashoffset={offset}
          style={{ transition: "stroke-dashoffset 1s ease" }}
        />
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-xl font-bold text-white">{score}%</span>
      </div>
    </div>
  );
}

export default function FeedbackPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const id = Number(sessionId);

  const { data: feedback, isLoading } = useQuery({
    queryKey: ["interview-feedback", id],
    queryFn: () => getInterviewFeedback(id),
    enabled: !!id,
    refetchInterval: (query: any) => {
      return query?.state?.data?.status === "FEEDBACK_PROCESSING" ? 2000 : false;
    },
  });

  if (isLoading) {
    return (
      <div className="max-w-3xl mx-auto space-y-4 animate-pulse">
        <div className="h-8 bg-gray-800 rounded w-1/3" />
        <div className="h-32 bg-gray-800 rounded-xl" />
        {[1, 2, 3].map((i) => (
          <div key={i} className="h-48 bg-gray-800 rounded-xl" />
        ))}
      </div>
    );
  }

  if (!feedback) {
    return (
      <div className="max-w-3xl mx-auto text-center py-16">
        <p className="text-gray-400 mb-4">Feedback not found or not yet generated.</p>
        <Link href="/interview" className="text-blue-400 hover:underline text-sm">
          ← Back to interviews
        </Link>
      </div>
    );
  }

  if ((feedback as any).status === "FEEDBACK_PROCESSING") {
    return (
      <div className="max-w-3xl mx-auto text-center py-20 space-y-6">
        <div className="w-12 h-12 border-4 border-blue-500/30 border-t-blue-500 rounded-full animate-spin mx-auto" />
        <div className="space-y-2">
          <h2 className="text-xl font-semibold text-white">Generating AI Feedback</h2>
          <p className="text-gray-400 text-sm max-w-md mx-auto">
            Gemini is evaluating your answers and grading them. This usually takes 10–20 seconds.
          </p>
        </div>
      </div>
    );
  }

  const overall = feedback.overall_score;
  const label =
    overall >= 75 ? "Strong performance 🎉" :
    overall >= 50 ? "Decent — keep improving 💪" :
    "Needs more practice 📚";

  return (
    <div className="max-w-3xl mx-auto space-y-6 pb-10">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-white">Interview feedback</h1>
          <p className="text-gray-400 text-sm mt-1">Session #{id} — AI-generated review</p>
        </div>
        <Link
          href="/interview"
          className="text-xs text-gray-400 hover:text-white transition border border-gray-700 hover:border-gray-600 px-3 py-1.5 rounded-lg"
        >
          ← All sessions
        </Link>
      </div>

      {/* Overall score card */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 flex items-center gap-6">
        <ScoreRing score={overall} />
        <div className="flex-1">
          <p className="text-xs text-gray-500 uppercase tracking-wider mb-1">Overall score</p>
          <p className="text-xl font-semibold text-white">{label}</p>
          <p className="text-gray-400 text-sm mt-2">
            {overall >= 75
              ? "You demonstrated strong technical and communication skills. Keep this momentum."
              : overall >= 50
              ? "You showed a good foundation. Focus on depth and structure in your answers."
              : "Review the feedback below carefully and revisit your roadmap weak areas."}
          </p>
          {/* Score breakdown bar */}
          <div className="mt-4 h-2 bg-gray-800 rounded-full overflow-hidden">
            <div
              className={`h-full rounded-full transition-all duration-700 ${
                overall >= 70 ? "bg-green-500" : overall >= 50 ? "bg-amber-500" : "bg-red-500"
              }`}
              style={{ width: `${overall}%` }}
            />
          </div>
        </div>
      </div>

      {/* Quick stats row */}
      <div className="grid grid-cols-3 gap-3">
        {[
          {
            label: "Questions",
            value: feedback.items.length,
            sub: "answered",
          },
          {
            label: "Strongest",
            value: `${Math.max(...feedback.items.map((f) => f.score))}%`,
            sub: "best answer",
          },
          {
            label: "Weakest",
            value: `${Math.min(...feedback.items.map((f) => f.score))}%`,
            sub: "needs work",
          },
        ].map((s) => (
          <div key={s.label} className="bg-gray-900 border border-gray-800 rounded-xl p-4 text-center">
            <p className="text-xs text-gray-500 mb-1">{s.label}</p>
            <p className="text-2xl font-semibold text-white">{s.value}</p>
            <p className="text-xs text-gray-600 mt-0.5">{s.sub}</p>
          </div>
        ))}
      </div>

      {/* Per-answer feedback */}
      <div>
        <h2 className="text-sm font-medium text-white mb-3">Per-question breakdown</h2>
        <div className="space-y-4">
          {feedback.items.map((item, i) => (
            <div
              key={item.question_id}
              className={`bg-gray-900 border rounded-xl overflow-hidden ${
                item.score >= 70
                  ? "border-green-500/20"
                  : item.score >= 50
                  ? "border-amber-500/20"
                  : "border-red-500/20"
              }`}
            >
              {/* Question header */}
              <div className="flex items-start justify-between gap-4 p-5 pb-3">
                <div className="flex items-start gap-3 min-w-0">
                  <span className="w-6 h-6 rounded-full bg-gray-800 text-gray-400 text-xs flex items-center justify-center flex-shrink-0 mt-0.5 font-medium">
                    {i + 1}
                  </span>
                  <p className="text-sm font-medium text-white leading-relaxed">
                    {item.question}
                  </p>
                </div>
                <ScoreBadge score={item.score} />
              </div>

              {/* Score mini-bar */}
              <div className="px-5 pb-3">
                <div className="h-1 bg-gray-800 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full ${
                      item.score >= 70 ? "bg-green-500" :
                      item.score >= 50 ? "bg-amber-500" : "bg-red-500"
                    }`}
                    style={{ width: `${item.score}%` }}
                  />
                </div>
              </div>

              {/* Your answer */}
              <div className="mx-5 mb-3 bg-gray-800/50 rounded-lg p-3">
                <p className="text-xs text-gray-500 mb-1.5 uppercase tracking-wider">Your answer</p>
                <p className="text-xs text-gray-400 leading-relaxed whitespace-pre-wrap">
                  {item.answer}
                </p>
              </div>

              {/* AI Feedback */}
              <div className="px-5 pb-5">
                <p className="text-xs text-gray-500 mb-2 uppercase tracking-wider">AI feedback</p>
                <div className="text-sm text-gray-300 leading-relaxed space-y-2 prose prose-invert prose-sm max-w-none">
                  {item.feedback.split("\n").filter(Boolean).map((line, li) => (
                    <p key={li}>{line}</p>
                  ))}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* CTA row */}
      <div className="grid grid-cols-2 gap-3">
        <Link
          href="/roadmap"
          className="bg-gray-900 border border-gray-800 hover:border-gray-700 text-gray-300 hover:text-white text-sm font-medium px-4 py-3 rounded-xl transition text-center"
        >
          📍 Go to roadmap
        </Link>
        <Link
          href="/interview"
          className="bg-blue-600 hover:bg-blue-500 text-white text-sm font-medium px-4 py-3 rounded-xl transition text-center"
        >
          ↻ Start another interview
        </Link>
      </div>
    </div>
  );
}
