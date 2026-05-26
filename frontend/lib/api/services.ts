import api from "./axios";
import type {
  LoginRequest,
  SignupRequest,
  AuthResponse,
  Profile,
  ProfileRequest,
  Resume,
  Roadmap,
  InterviewSession,
  AnswerSubmission,
  InterviewFeedback,
  Progress,
  DashboardData,
} from "@/types";

// ─── Auth ────────────────────────────────────────────────────────────────────

export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  const res = await api.post("/auth/login", data);
  return res.data;
};

export const signup = async (data: SignupRequest): Promise<AuthResponse> => {
  const res = await api.post("/auth/signup", data);
  return res.data;
};

export const getCurrentUser = async () => {
  const res = await api.get("/users/me");
  return res.data;
};

// ─── Profile ─────────────────────────────────────────────────────────────────

export const getProfile = async (): Promise<Profile> => {
  const res = await api.get("/profile");
  return res.data;
};

export const createProfile = async (data: ProfileRequest): Promise<Profile> => {
  const res = await api.post("/profile", data);
  return res.data;
};

export const updateProfile = async (data: ProfileRequest): Promise<Profile> => {
  const res = await api.put("/profile", data);
  return res.data;
};

// ─── Resume ──────────────────────────────────────────────────────────────────

export const uploadResume = async (file: File): Promise<Resume> => {
  const formData = new FormData();
  formData.append("file", file);
  const res = await api.post("/resume/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};

export const getResume = async (): Promise<Resume> => {
  const res = await api.get("/resume");
  return res.data;
};

// ─── Roadmap ─────────────────────────────────────────────────────────────────

export const getRoadmap = async (): Promise<Roadmap> => {
  const res = await api.get("/roadmap");
  return res.data;
};

export const generateRoadmap = async (): Promise<Roadmap> => {
  const res = await api.post("/roadmap/generate");
  return res.data;
};

// ─── Interview ───────────────────────────────────────────────────────────────

export const generateInterview = async (role: string): Promise<InterviewSession> => {
  const res = await api.post("/interviews/generate", { role });
  return res.data;
};

export const getInterviewSession = async (sessionId: number): Promise<InterviewSession> => {
  const res = await api.get(`/interviews/${sessionId}`);
  return res.data;
};

export const getAllInterviews = async (): Promise<InterviewSession[]> => {
  const res = await api.get("/interviews");
  return res.data;
};

export const submitAnswers = async (
  sessionId: number,
  answers: AnswerSubmission[]
): Promise<void> => {
  await api.post(`/interviews/${sessionId}/submit`, { answers });
};

export const getInterviewFeedback = async (
  sessionId: number
): Promise<InterviewFeedback> => {
  const res = await api.get(`/interviews/${sessionId}/feedback`);
  return res.data;
};

// ─── Progress ────────────────────────────────────────────────────────────────

export const getProgress = async (): Promise<Progress> => {
  const res = await api.get("/progress");
  return res.data;
};

export const completeTask = async (taskId: string): Promise<void> => {
  await api.post("/progress/complete", { task_id: taskId });
};

export const uncompleteTask = async (taskId: string): Promise<void> => {
  await api.post("/progress/uncomplete", { task_id: taskId });
};

export const getDashboard = async (): Promise<DashboardData> => {
  const res = await api.get("/progress/dashboard");
  return res.data;
};
