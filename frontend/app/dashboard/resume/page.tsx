"use client";

import { useCallback, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { uploadResume, getResume } from "@/lib/api/services";

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

export default function ResumePage() {
  const queryClient = useQueryClient();
  const [dragOver, setDragOver] = useState(false);
  const [uploadError, setUploadError] = useState("");
  const [showText, setShowText] = useState(false);

  const { data: resume, isLoading } = useQuery({
    queryKey: ["resume"],
    queryFn: getResume,
  });

  const { mutate: upload, isPending } = useMutation({
    mutationFn: uploadResume,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["resume"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      setUploadError("");
    },
    onError: (err: unknown) => {
      const msg =
        (err as { response?: { data?: { detail?: string } } })?.response?.data
          ?.detail ?? "Upload failed. Please try again.";
      setUploadError(msg);
    },
  });

  const handleFile = useCallback(
    (file: File) => {
      setUploadError("");
      if (file.type !== "application/pdf") {
        setUploadError("Only PDF files are supported.");
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        setUploadError("File must be under 5 MB.");
        return;
      }
      upload(file);
    },
    [upload]
  );

  const onDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      setDragOver(false);
      const file = e.dataTransfer.files[0];
      if (file) handleFile(file);
    },
    [handleFile]
  );

  const onInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) handleFile(file);
    e.target.value = "";
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-semibold text-white">Resume</h1>
        <p className="text-gray-400 text-sm mt-1">
          Upload your PDF resume. It&apos;s used to personalize your AI
          interview questions.
        </p>
      </div>

      {/* Upload zone */}
      <div
        onDragOver={(e) => {
          e.preventDefault();
          setDragOver(true);
        }}
        onDragLeave={() => setDragOver(false)}
        onDrop={onDrop}
        className={`relative border-2 border-dashed rounded-xl p-10 text-center transition-all ${
          dragOver
            ? "border-blue-500 bg-blue-500/5"
            : isPending
            ? "border-gray-700 bg-gray-900/50"
            : "border-gray-700 hover:border-gray-600 bg-gray-900/30 hover:bg-gray-900/50"
        }`}
      >
        <input
          type="file"
          accept=".pdf,application/pdf"
          onChange={onInputChange}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
          disabled={isPending}
        />

        {isPending ? (
          <div className="space-y-3">
            <div className="w-10 h-10 border-2 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto" />
            <p className="text-blue-400 text-sm font-medium">
              Uploading and extracting text...
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            <div className="text-4xl">📄</div>
            <div>
              <p className="text-white text-sm font-medium">
                Drag & drop your PDF here
              </p>
              <p className="text-gray-500 text-xs mt-1">
                or click anywhere to browse · Max 5 MB
              </p>
            </div>
            <span className="inline-block bg-gray-800 hover:bg-gray-700 text-gray-300 text-xs font-medium px-4 py-2 rounded-lg transition">
              Choose file
            </span>
          </div>
        )}
      </div>

      {/* Upload error */}
      {uploadError && (
        <div className="bg-red-500/10 border border-red-500/20 text-red-400 text-sm rounded-xl px-4 py-3">
          {uploadError}
        </div>
      )}

      {/* Current resume card */}
      {isLoading ? (
        <div className="h-24 bg-gray-800 rounded-xl animate-pulse" />
      ) : resume ? (
        <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden">
          {/* Resume header */}
          <div className="flex items-center justify-between p-5">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-red-500/10 border border-red-500/20 rounded-lg flex items-center justify-center text-red-400 text-sm font-bold flex-shrink-0">
                PDF
              </div>
              <div>
                <p className="text-white text-sm font-medium">
                  {resume.filename}
                </p>
                <p className="text-gray-500 text-xs">
                  Uploaded {formatDate(resume.uploaded_at)}
                </p>
              </div>
            </div>
            <span className="text-xs bg-green-500/10 border border-green-500/20 text-green-400 px-2.5 py-1 rounded-full">
              ✓ Active
            </span>
          </div>

          {/* Divider */}
          <div className="border-t border-gray-800" />

          {/* Extracted text toggle */}
          <div className="p-5">
            <button
              onClick={() => setShowText((s) => !s)}
              className="flex items-center gap-2 text-xs text-gray-400 hover:text-white transition"
            >
              <svg
                className={`w-3.5 h-3.5 transition-transform ${
                  showText ? "rotate-90" : ""
                }`}
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
              {showText ? "Hide" : "View"} extracted text (what the AI sees)
            </button>

            {showText && resume.extracted_text && (
              <div className="mt-3 bg-gray-800/60 rounded-lg p-4 max-h-64 overflow-y-auto">
                <pre className="text-xs text-gray-400 whitespace-pre-wrap leading-relaxed font-mono">
                  {resume.extracted_text}
                </pre>
              </div>
            )}
          </div>

          {/* Replace note */}
          <div className="px-5 pb-4">
            <p className="text-xs text-gray-600">
              Upload a new PDF above to replace this resume.
            </p>
          </div>
        </div>
      ) : (
        <div className="bg-gray-900/50 border border-gray-800 rounded-xl p-6 text-center">
          <p className="text-gray-500 text-sm">
            No resume uploaded yet. Upload one above to unlock AI interviews
            tailored to your experience.
          </p>
        </div>
      )}

      {/* Info card */}
      <div className="bg-blue-500/5 border border-blue-500/15 rounded-xl p-4">
        <p className="text-blue-400 text-xs font-medium mb-1">
          How your resume is used
        </p>
        <p className="text-gray-500 text-xs leading-relaxed">
          When you start an AI interview, Gemini reads your resume along with
          your target role to generate relevant, personalized questions — just
          like a real recruiter would.
        </p>
      </div>
    </div>
  );
}
