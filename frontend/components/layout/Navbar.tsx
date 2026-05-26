"use client";

import { usePathname } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";

const pageTitles: Record<string, string> = {
  "/dashboard": "Dashboard",
  "/dashboard/roadmap": "Roadmap",
  "/dashboard/interview": "Interview",
  "/dashboard/resume": "Resume",
  "/dashboard/progress": "Progress",
  "/dashboard/analytics": "Analytics",
  "/dashboard/profile": "Profile",
};

export default function Navbar() {
  const pathname = usePathname();
  const { user } = useAuthStore();

  // Match the most specific route
  const title =
    Object.entries(pageTitles)
      .sort((a, b) => b[0].length - a[0].length)
      .find(([key]) => pathname.startsWith(key))?.[1] ?? "Placementor";

  return (
    <header className="h-14 bg-gray-950/80 backdrop-blur border-b border-gray-800/60 flex items-center justify-between px-6 sticky top-0 z-30">
      {/* Left: Title */}
      <div className="flex items-center gap-2 text-sm">
        <Link
          href="/dashboard"
          className="text-gray-500 hover:text-gray-300 transition"
        >
          Placementor
        </Link>
        <span className="text-gray-700">/</span>
        <span className="text-white font-medium">{title}</span>
      </div>

      {/* Right: User + CTA */}
      <div className="flex items-center gap-3">
        <Link
          href="/dashboard/interview"
          className="bg-blue-600 hover:bg-blue-500 text-white text-xs font-medium px-3 py-1.5 rounded-lg transition"
        >
          + Start Interview
        </Link>
        <div className="w-7 h-7 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs font-bold">
          {user?.username?.charAt(0).toUpperCase() ?? "U"}
        </div>
      </div>
    </header>
  );
}
