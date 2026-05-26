"use client";

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getProfile, createProfile, updateProfile } from "@/lib/api/services";
import type { ProfileRequest } from "@/types";

const schema = z.object({
  target_role: z.string().min(2, "Required"),
  domain: z.string().min(2, "Required"),
  current_level: z.string().min(1, "Required"),
  target_company: z.string().optional(),
  github_url: z
    .string()
    .url("Enter a valid URL")
    .optional()
    .or(z.literal("")),
  leetcode_url: z
    .string()
    .url("Enter a valid URL")
    .optional()
    .or(z.literal("")),
  codeforces_url: z
    .string()
    .url("Enter a valid URL")
    .optional()
    .or(z.literal("")),
});

type FormData = z.infer<typeof schema>;

const roles = [
  "Software Development Engineer (SDE)",
  "Data Scientist",
  "Machine Learning Engineer",
  "Frontend Developer",
  "Backend Developer",
  "Full Stack Developer",
  "DevOps Engineer",
  "Product Manager",
];

const domains = [
  "Computer Science",
  "Information Technology",
  "Electronics & Communication",
  "Mechanical Engineering",
  "MBA / Management",
  "Data Science",
  "Artificial Intelligence",
];

const levels = [
  "1st Year",
  "2nd Year",
  "3rd Year",
  "4th Year",
  "Recent Graduate",
  "Experienced Professional",
];

const companies = [
  "Google",
  "Microsoft",
  "Amazon",
  "Meta",
  "Apple",
  "Netflix",
  "Flipkart",
  "Swiggy",
  "Zomato",
  "Paytm",
  "Infosys",
  "TCS",
  "Wipro",
  "Accenture",
  "Other",
];

function Field({
  label,
  error,
  children,
}: {
  label: string;
  error?: string;
  children: React.ReactNode;
}) {
  return (
    <div>
      <label className="block text-sm text-gray-400 mb-1.5">{label}</label>
      {children}
      {error && <p className="text-red-400 text-xs mt-1">{error}</p>}
    </div>
  );
}

const inputCls =
  "w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:border-blue-500 transition placeholder:text-gray-600";

const selectCls =
  "w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:border-blue-500 transition appearance-none";

export default function ProfilePage() {
  const queryClient = useQueryClient();
  const [saved, setSaved] = useState(false);

  const { data: profile, isLoading } = useQuery({
    queryKey: ["profile"],
    queryFn: getProfile,
  });

  const isNew = !profile;

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isDirty },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  // Pre-fill form when profile loads
  useEffect(() => {
    if (profile) {
      reset({
        target_role: profile.target_role,
        domain: profile.domain,
        current_level: profile.current_level,
        target_company: profile.target_company ?? "",
        github_url: profile.github_url ?? "",
        leetcode_url: profile.leetcode_url ?? "",
        codeforces_url: profile.codeforces_url ?? "",
      });
    }
  }, [profile, reset]);

  const { mutate, isPending } = useMutation({
    mutationFn: (data: ProfileRequest) =>
      isNew ? createProfile(data) : updateProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["profile"] });
      queryClient.invalidateQueries({ queryKey: ["roadmap"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    },
  });

  const onSubmit = (data: FormData) => {
    // Strip empty optional strings
    const payload: ProfileRequest = {
      target_role: data.target_role,
      domain: data.domain,
      current_level: data.current_level,
      target_company: data.target_company || undefined,
      github_url: data.github_url || undefined,
      leetcode_url: data.leetcode_url || undefined,
      codeforces_url: data.codeforces_url || undefined,
    };
    mutate(payload);
  };

  if (isLoading) {
    return (
      <div className="max-w-2xl mx-auto space-y-4 animate-pulse">
        {[...Array(6)].map((_, i) => (
          <div key={i} className="h-12 bg-gray-800 rounded-lg" />
        ))}
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-semibold text-white">Your profile</h1>
        <p className="text-gray-400 text-sm mt-1">
          This drives your personalized roadmap and AI interview questions.
        </p>
      </div>

      {/* Success banner */}
      {saved && (
        <div className="mb-4 bg-green-500/10 border border-green-500/20 text-green-400 text-sm rounded-xl px-4 py-3">
          ✓ Profile saved successfully
          {isNew && " — your roadmap has been generated!"}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        {/* Card: Placement goals */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 space-y-4">
          <h2 className="text-sm font-medium text-white mb-1">
            Placement goals
          </h2>

          <Field label="Target role *" error={errors.target_role?.message}>
            <select {...register("target_role")} className={selectCls}>
              <option value="">Select a role</option>
              {roles.map((r) => (
                <option key={r} value={r}>
                  {r}
                </option>
              ))}
            </select>
          </Field>

          <Field label="Domain / Branch *" error={errors.domain?.message}>
            <select {...register("domain")} className={selectCls}>
              <option value="">Select your domain</option>
              {domains.map((d) => (
                <option key={d} value={d}>
                  {d}
                </option>
              ))}
            </select>
          </Field>

          <Field
            label="Current level *"
            error={errors.current_level?.message}
          >
            <select {...register("current_level")} className={selectCls}>
              <option value="">Select your year</option>
              {levels.map((l) => (
                <option key={l} value={l}>
                  {l}
                </option>
              ))}
            </select>
          </Field>

          <Field
            label="Target company"
            error={errors.target_company?.message}
          >
            <select {...register("target_company")} className={selectCls}>
              <option value="">Select a company (optional)</option>
              {companies.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          </Field>
        </div>

        {/* Card: Coding profiles */}
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-6 space-y-4">
          <h2 className="text-sm font-medium text-white mb-1">
            Coding profiles{" "}
            <span className="text-gray-600 font-normal">(optional)</span>
          </h2>

          <Field label="GitHub URL" error={errors.github_url?.message}>
            <input
              {...register("github_url")}
              placeholder="https://github.com/username"
              className={inputCls}
            />
          </Field>

          <Field label="LeetCode URL" error={errors.leetcode_url?.message}>
            <input
              {...register("leetcode_url")}
              placeholder="https://leetcode.com/username"
              className={inputCls}
            />
          </Field>

          <Field
            label="Codeforces URL"
            error={errors.codeforces_url?.message}
          >
            <input
              {...register("codeforces_url")}
              placeholder="https://codeforces.com/profile/username"
              className={inputCls}
            />
          </Field>
        </div>

        {/* Submit */}
        <button
          type="submit"
          disabled={isPending || (!isDirty && !isNew)}
          className="w-full bg-blue-600 hover:bg-blue-500 disabled:opacity-40 disabled:cursor-not-allowed text-white font-medium rounded-xl py-3 text-sm transition"
        >
          {isPending
            ? "Saving..."
            : isNew
            ? "Save profile & generate roadmap"
            : "Update profile"}
        </button>
      </form>
    </div>
  );
}
