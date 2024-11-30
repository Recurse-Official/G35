"use client";

import { useEffect } from "react";
import { NavBar } from "@/components/navBar";
import "./globals.css";
export default function RootLayout({ children }: { children: React.ReactNode }) {
    useEffect(() => {
        if (typeof window !== "undefined") {
            const isLoggedIn = localStorage.getItem("loggedIn");

            if (!isLoggedIn) {
                const currentPath = window.location.pathname;
                if (currentPath !== "/login" && currentPath !== "/signup") {
                    window.location.href = "/login";
                }
            }
        }
    }, []);

    return (
      <html lang="en">
        <head>
          <meta charSet="utf-8" />
          <meta name="viewport" content="width=device-width" />
          {/* <meta name="description" content={metadata.description || ""} />
          <link rel="icon" href={metadata.icons[0]} />
          <title>{metadata.title}</title> */}
        </head>
        <body>
          <NavBar />
          {/* <SideBar /> */}
          <div className="content">{children}</div>
        </body>
      </html>
    )
}
