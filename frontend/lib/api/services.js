import api from "./axios";

// ─── Auth ────────────────────────────────────────────────────────────────────

export const login = async (data) => {
  const res = await api.post("/auth/login", data);
  return res.data;
};

export const signup = async (data) => {
  const res = await api.post("/auth/signup", data);
  return res.data;
};

export const getCurrentUser = async () => {
  const res = await api.get("/users/me");
  return res.data;
};

// ─── Profile ─────────────────────────────────────────────────────────────────

export const getProfile = async () => {
  const res = await api.get(`/profile/?t=${Date.now()}`, {
    headers: { "Cache-Control": "no-cache" },
  });
  if (!res.data || !res.data.id) return null;
  return res.data;
};

export const createProfile = async (data) => {
  const res = await api.post("/profile/", data);
  return res.data;
};

export const updateProfile = async (data) => {
  const res = await api.put("/profile/", data);
  return res.data;
};

// ─── Resume ──────────────────────────────────────────────────────────────────

export const uploadResume = async (file) => {
  const formData = new FormData();
  formData.append("file", file);
  const res = await api.post("/resume/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return res.data;
};

export const getResume = async () => {
  const res = await api.get("/resume");
  return res.data;
};

// ─── Roadmap ─────────────────────────────────────────────────────────────────

export const getRoadmap = async () => {
  const res = await api.get("/roadmap");
  return res.data;
};

export const generateRoadmap = async () => {
  const res = await api.post("/roadmap/generate");
  return res.data;
};

// ─── Interview ───────────────────────────────────────────────────────────────

export const generateInterview = async (role) => {
  const res = await api.post("/interviews/generate", { role });
  return res.data;
};

export const getInterviewSession = async (sessionId) => {
  const res = await api.get(`/interviews/${sessionId}`);
  return res.data;
};

export const getAllInterviews = async () => {
  const res = await api.get("/interviews");
  return res.data;
};

export const submitAnswers = async (sessionId, answers) => {
  await api.post(`/interviews/${sessionId}/submit`, { answers });
};

export const getInterviewFeedback = async (sessionId) => {
  const res = await api.get(`/interviews/${sessionId}/feedback`);
  return res.data;
};

// ─── Progress ────────────────────────────────────────────────────────────────

export const getProgress = async () => {
  const res = await api.get("/progress");
  return res.data;
};

export const completeTask = async (taskId) => {
  await api.post("/progress/complete", { task_name: taskId });
};

export const uncompleteTask = async (taskId) => {
  await api.post("/progress/uncomplete", { task_name: taskId });
};

export const getDashboard = async () => {
  const res = await api.get("/progress/dashboard");
  return res.data;
};
