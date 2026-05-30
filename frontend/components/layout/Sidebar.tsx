"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";

const navItems = [
  {
    icon: "⊞",
    label: "Dashboard",
    href: "/dashboard",
  },
  {
    icon: "⬡",
    label: "Roadmap",
    href: "/dashboard/roadmap",
  },
  {
    icon: "◎",
    label: "Interview",
    href: "/dashboard/interview",
  },
  {
    icon: "⬆",
    label: "Resume",
    href: "/dashboard/resume",
  },
  {
    icon: "◈",
    label: "Progress",
    href: "/dashboard/progress",
  },
  {
    icon: "▦",
    label: "Analytics",
    href: "/dashboard/analytics",
  },
  {
    icon: "◯",
    label: "Profile",
    href: "/dashboard/profile",
  },
];

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const { user, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    document.cookie =
      "placementor-token=; path=/; max-age=0";
    router.push("/login");
  };

  return (
    <aside className="fixed left-0 top-0 h-screen w-60 bg-gray-950 border-r border-gray-800/60 flex flex-col z-40">
      {/* Logo */}
      <div className="px-6 py-5 border-b border-gray-800/60">
        <span className="text-xl font-bold text-white tracking-tight">
          Place<span className="text-blue-500">mentor</span>
        </span>
      </div>

      {/* Nav links */}
      <nav className="flex-1 px-3 py-4 space-y-0.5 overflow-y-auto">
        {navItems.map((item) => {
          const isActive =
            pathname === item.href ||
            pathname.startsWith(item.href + "/");
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                isActive
                  ? "bg-blue-600/20 text-blue-400 border border-blue-500/20"
                  : "text-gray-400 hover:text-white hover:bg-gray-800/60"
              }`}
            >
              <span className="text-base w-5 text-center">{item.icon}</span>
              {item.label}
            </Link>
          );
        })}
      </nav>

      {/* User info + logout */}
      <div className="px-4 py-4 border-t border-gray-800/60">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
            {user?.email?.charAt(0).toUpperCase() ?? "U"}
          </div>
          <div className="min-w-0">
            <p className="text-white text-sm font-medium truncate">
              {user?.email ?? "User"}
            </p>
            <p className="text-gray-500 text-xs truncate">
              {user?.email ?? ""}
            </p>
          </div>
        </div>
        <button
          onClick={handleLogout}
          className="w-full text-left text-xs text-gray-500 hover:text-red-400 transition px-1 py-1"
        >
          → Sign out
        </button>
      </div>
    </aside>
  );
}
