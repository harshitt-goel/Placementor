// ─── Auth ────────────────────────────────────────────────────────────────────

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  email: string;
  username: string;
  password: string;
}

export interface AuthResponse {
  access_token: string;
  token_type: string;
  user: {
    id: number;
    email: string;
    username: string;
  };
}

// ─── Profile ─────────────────────────────────────────────────────────────────

export interface Profile {
  id: number;
  user_id: number;
  target_role: string;
  domain: string;
  current_level: string;
  github_url?: string;
  leetcode_url?: string;
  codeforces_url?: string;
  target_company?: string;
}

export interface ProfileRequest {
  target_role: string;
  domain: string;
  current_level: string;
  github_url?: string;
  leetcode_url?: string;
  codeforces_url?: string;
  target_company?: string;
}

// ─── Resume ──────────────────────────────────────────────────────────────────

export interface Resume {
  id: number;
  user_id: number;
  filename: string;
  extracted_text: string;
  uploaded_at: string;
}

// ─── Roadmap ─────────────────────────────────────────────────────────────────

export interface Task {
  id: string;
  title: string;
  description: string;
  resources?: string[];
}

export interface Phase {
  phase_number: number;
  title: string;
  description: string;
  tasks: Task[];
}

export interface Roadmap {
  id: number;
  user_id: number;
  role: string;
  phases: Phase[];
  created_at: string;
}

// ─── Interview ───────────────────────────────────────────────────────────────

export interface InterviewQuestion {
  id: string;
  question: string;
}

export interface InterviewSession {
  id: number;
  user_id: number;
  role: string;
  questions: InterviewQuestion[];
  created_at: string;
  submitted: boolean;
}

export interface AnswerSubmission {
  question_id: string;
  answer: string;
}

export interface FeedbackItem {
  question_id: string;
  question: string;
  answer: string;
  feedback: string;
  score: number; // 0–100
}

export interface InterviewFeedback {
  session_id: number;
  overall_score: number;
  items: FeedbackItem[];
}

// ─── Progress ────────────────────────────────────────────────────────────────

export interface Progress {
  completed_task_ids: string[];
  total_tasks: number;
  completed_tasks: number;
  percentage: number;
}

export interface DashboardData {
  readiness_score: number;
  roadmap_percentage: number;
  total_interviews: number;
  average_interview_score: number;
  recent_interviews: RecentInterview[];
}

export interface RecentInterview {
  id: number;
  role: string;
  score: number;
  created_at: string;
}
