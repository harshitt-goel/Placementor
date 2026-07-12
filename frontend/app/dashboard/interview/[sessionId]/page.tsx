"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { useQuery, useMutation } from "@tanstack/react-query";
import { getInterviewSession, submitAnswers } from "@/lib/api/services";
import type { AnswerSubmission } from "@/types";

export default function InterviewSessionPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const router = useRouter();
  const id = Number(sessionId);

  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [submitError, setSubmitError] = useState("");
  const [activeQ, setActiveQ] = useState(0);

  const { data: session, isLoading } = useQuery({
    queryKey: ["interview-session", id],
    queryFn: () => getInterviewSession(id),
    enabled: !!id,
    refetchInterval: (query: any) => {
      return query?.state?.data?.status === "PROCESSING" ? 2000 : false;
    },
  });

  // If already submitted, redirect to feedback
  useEffect(() => {
    if (session?.submitted) {
      router.replace(`/dashboard/interview/${id}/feedback`);
    }
  }, [session, id, router]);

  const { mutate: submit, isPending: submitting } = useMutation({
    mutationFn: (data: AnswerSubmission[]) => submitAnswers(id, data),
    onSuccess: () => {
      router.push(`/dashboard/interview/${id}/feedback`);
    },
    onError: (err: unknown) => {
      const msg =
        (err as { response?: { data?: { detail?: string } } })?.response?.data
          ?.detail ?? "Submission failed. Please try again.";
      setSubmitError(msg);
    },
  });

  const handleSubmit = () => {
    setSubmitError("");
    if (!session) return;

    const missing = session.questions.filter(
      (q) => !answers[q.id]?.trim()
    );
    if (missing.length > 0) {
      setSubmitError(
        `Please answer all questions. ${missing.length} unanswered.`
      );
      return;
    }

    const payload: AnswerSubmission[] = session.questions.map((q) => ({
      question_id: q.id,
      answer: answers[q.id].trim(),
    }));
    submit(payload);
  };

  const answeredCount = session
    ? session.questions.filter((q) => answers[q.id]?.trim()).length
    : 0;
  const totalCount = session?.questions.length ?? 0;
  const allAnswered = answeredCount === totalCount && totalCount > 0;

  // ── Loading ───────────────────────────────────────────────────────────────
  if (isLoading) {
    return (
      <div className="max-w-3xl mx-auto space-y-4 animate-pulse">
        <div className="h-8 bg-gray-800 rounded w-1/2" />
        <div className="h-48 bg-gray-800 rounded-xl" />
        <div className="h-32 bg-gray-800 rounded-xl" />
      </div>
    );
  }

  if (!session) {
    return (
      <div className="max-w-3xl mx-auto text-center py-16">
        <p className="text-gray-400">Session not found.</p>
      </div>
    );
  }

  if ((session as any).status === "PROCESSING") {
    return (
      <div className="max-w-3xl mx-auto text-center py-20 space-y-6">
        <div className="w-12 h-12 border-4 border-blue-500/30 border-t-blue-500 rounded-full animate-spin mx-auto" />
        <div className="space-y-2">
          <h2 className="text-xl font-semibold text-white">Generating Interview Questions</h2>
          <p className="text-gray-400 text-sm max-w-md mx-auto">
            Gemini is analyzing your resume for the <span className="text-blue-400 font-medium">{session.role}</span> role. This usually takes 10–20 seconds.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto space-y-6 pb-32">
      {/* Header */}
      <div>
        <div className="flex items-center gap-3 mb-1">
          <h1 className="text-2xl font-semibold text-white">
            {session.role}
          </h1>
          <span className="text-xs bg-blue-500/10 border border-blue-500/20 text-blue-400 px-2.5 py-1 rounded-full">
            Session #{session.id}
          </span>
        </div>
        <p className="text-gray-400 text-sm">
          Answer all {totalCount} questions honestly — the AI feedback is based
          on your actual responses.
        </p>
      </div>

      {/* Progress bar */}
      <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
        <div className="flex justify-between items-center mb-2">
          <span className="text-xs text-gray-400">
            {answeredCount} of {totalCount} answered
          </span>
          <span className="text-xs text-gray-500">
            {Math.round((answeredCount / totalCount) * 100)}%
          </span>
        </div>
        <div className="h-1.5 bg-gray-800 rounded-full overflow-hidden">
          <div
            className="h-full bg-blue-500 rounded-full transition-all duration-500"
            style={{
              width: `${(answeredCount / totalCount) * 100}%`,
            }}
          />
        </div>
        {/* Question jump pills */}
        <div className="flex flex-wrap gap-1.5 mt-3">
          {session.questions.map((q, i) => (
            <button
              key={q.id}
              onClick={() => setActiveQ(i)}
              className={`w-7 h-7 rounded-full text-xs font-medium transition-all ${
                i === activeQ
                  ? "bg-blue-600 text-white"
                  : answers[q.id]?.trim()
                  ? "bg-green-500/20 text-green-400 border border-green-500/30"
                  : "bg-gray-800 text-gray-500 hover:bg-gray-700"
              }`}
            >
              {i + 1}
            </button>
          ))}
        </div>
      </div>

      {/* Questions */}
      <div className="space-y-4">
        {session.questions.map((q, i) => {
          const answered = !!answers[q.id]?.trim();
          const isActive = i === activeQ;

          return (
            <div
              key={q.id}
              id={`q-${i}`}
              className={`bg-gray-900 border rounded-xl overflow-hidden transition-all ${
                isActive
                  ? "border-blue-500/40"
                  : answered
                  ? "border-green-500/20"
                  : "border-gray-800"
              }`}
            >
              {/* Question header */}
              <button
                onClick={() => setActiveQ(isActive ? -1 : i)}
                className="w-full text-left p-5 flex items-start gap-4"
              >
                <span
                  className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 transition-colors ${
                    answered
                      ? "bg-green-500 text-white"
                      : isActive
                      ? "bg-blue-600 text-white"
                      : "bg-gray-800 text-gray-400"
                  }`}
                >
                  {answered ? "✓" : i + 1}
                </span>
                <p className="text-sm font-medium text-gray-200 leading-relaxed text-left">
                  {q.question}
                </p>
              </button>

              {/* Answer area — only show when active */}
              {isActive && (
                <div className="px-5 pb-5">
                  <textarea
                    value={answers[q.id] ?? ""}
                    onChange={(e) =>
                      setAnswers((prev) => ({
                        ...prev,
                        [q.id]: e.target.value,
                      }))
                    }
                    placeholder="Type your answer here... Be specific and use examples where relevant."
                    rows={6}
                    className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-blue-500 transition resize-none placeholder:text-gray-600 leading-relaxed"
                  />
                  <div className="flex items-center justify-between mt-2">
                    <span className="text-xs text-gray-600">
                      {answers[q.id]?.length ?? 0} characters
                    </span>
                    {i < session.questions.length - 1 && (
                      <button
                        onClick={() => setActiveQ(i + 1)}
                        className="text-xs text-blue-400 hover:text-blue-300 transition"
                      >
                        Next question →
                      </button>
                    )}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Sticky submit bar */}
      <div className="fixed bottom-0 left-60 right-0 bg-gray-950/95 backdrop-blur border-t border-gray-800 p-4">
        <div className="max-w-3xl mx-auto flex items-center justify-between gap-4">
          <div>
            {submitError && (
              <p className="text-red-400 text-xs">{submitError}</p>
            )}
            {!allAnswered && !submitError && (
              <p className="text-gray-500 text-xs">
                {totalCount - answeredCount} question
                {totalCount - answeredCount !== 1 ? "s" : ""} remaining
              </p>
            )}
            {allAnswered && (
              <p className="text-green-400 text-xs">
                ✓ All questions answered — ready to submit!
              </p>
            )}
          </div>
          <button
            onClick={handleSubmit}
            disabled={submitting || !allAnswered}
            className="bg-blue-600 hover:bg-blue-500 disabled:opacity-40 disabled:cursor-not-allowed text-white font-medium px-6 py-2.5 rounded-lg text-sm transition flex items-center gap-2 flex-shrink-0"
          >
            {submitting ? (
              <>
                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                Getting AI feedback...
              </>
            ) : (
              "Submit for AI feedback →"
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
