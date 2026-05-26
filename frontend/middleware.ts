import { NextRequest, NextResponse } from "next/server";

export function middleware(req: NextRequest) {
  const { pathname } = req.nextUrl;

  // Routes that require authentication
  const protectedPrefixes = [
    "/dashboard",
    "/roadmap",
    "/interview",
    "/resume",
    "/progress",
    "/profile",
    "/analytics",
  ];

  const isProtected = protectedPrefixes.some((prefix) =>
    pathname.startsWith(prefix)
  );

  if (isProtected) {
    // Check for token in cookies (for SSR-safe auth)
    const token = req.cookies.get("placementor-token")?.value;

    if (!token) {
      // Redirect to login, preserving the intended destination
      const loginUrl = new URL("/login", req.url);
      loginUrl.searchParams.set("redirect", pathname);
      return NextResponse.redirect(loginUrl);
    }
  }

  // Redirect logged-in users away from auth pages
  const authRoutes = ["/login", "/signup"];
  const isAuthRoute = authRoutes.includes(pathname);
  if (isAuthRoute) {
    const token = req.cookies.get("placementor-token")?.value;
    if (token) {
      return NextResponse.redirect(new URL("/dashboard", req.url));
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    "/dashboard/:path*",
    "/roadmap/:path*",
    "/interview/:path*",
    "/resume/:path*",
    "/progress/:path*",
    "/profile/:path*",
    "/analytics/:path*",
    "/login",
    "/signup",
  ],
};
