import Link from "next/link";
import './styles/sideBar.css';

export function SideBar() {
    return (
      <div className="sidebar">
        <div className="sidebar-item">
          <Link href="/home">Home</Link>
        </div>
        <div className="sidebar-item">
          <Link href="/find">Find Food</Link>
        </div>
        <div className="sidebar-item">
          <Link href="/donate">Donate Food</Link>
        </div>
        <div className="sidebar-item">
          <Link href="/profile">Profile</Link>
        </div>
      </div>
    );
}