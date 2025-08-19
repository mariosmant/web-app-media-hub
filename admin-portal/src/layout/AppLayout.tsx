import { Outlet } from 'react-router-dom';
import { Navbar } from '../components/Navbar';
import { SkipLink } from '../components/SkipLink';

export default function AppLayout() {
  return (
    <>
      <SkipLink targetId="main-content" />
      <Navbar />
      <main id="main-content" className="app-main" tabIndex={-1}>
        <div className="container py-4">
          <Outlet />
        </div>
      </main>
    </>
  );
}
