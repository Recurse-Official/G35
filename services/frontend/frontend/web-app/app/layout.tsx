import { SideBar } from "@/components/sideBar"
import { NavBar } from "@/components/navBar"
import "./globals.css"

export const metadata = {
  title: 'Food',
  description: 'Food redistribution platform',
  icons: ["/logo.ico"]
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width" />
        <meta name="description" content={metadata.description || ""} />
        <link rel="icon" href={metadata.icons[0]} />
        <title>{metadata.title}</title>
      </head>
      <body>
        <NavBar />
        <SideBar />
        <div className="content">{children}</div>
      </body>
    </html>
  )
}
