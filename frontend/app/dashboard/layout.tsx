import Sidebar from "@/components/layout/Sidebar";
import Navbar from "@/components/layout/Navbar";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen bg-gray-950 text-white">
      {/* Sidebar — fixed left */}
      <Sidebar />

      {/* Main content — offset by sidebar width */}
      <div className="ml-60 flex flex-col min-h-screen">
        {/* Sticky top navbar */}
        <Navbar />

        {/* Page content */}
        <main className="flex-1 p-6">
          {children}
        </main>
      </div>
    </div>
  );
}
