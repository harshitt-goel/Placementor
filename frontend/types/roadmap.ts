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
  id: string;
  role: string;
  phases: Phase[];
  needs_regeneration?: boolean;
  current_profile_role?: string;
}