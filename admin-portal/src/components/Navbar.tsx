import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from 'react-oidc-context';
import { useI18n } from '../i18n/i18n';

// Preload likely routes on intent
const preloadHome = () => import('../routes/home');
const preloadUsers = () => import('../routes/users');

export function Navbar() {
  const auth = useAuth();
  const { t, locale, setLocale } = useI18n();
  const navigate = useNavigate();
  const location = useLocation();

  const getLinkClass = ({ isActive }: { isActive: boolean }) =>
    `nav-link${isActive ? ' active' : ''}`;

  const onLogin = () => {
    void auth.signinRedirect({
      state: { returnTo: location.pathname + location.search },
    });
  };

  const onLogout = () => {
    void auth.signoutRedirect();
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm" aria-label="Main">
      <div className="container">
        <NavLink
          className="navbar-brand fw-bold"
          to="/"
          onMouseEnter={preloadHome}
          onFocus={preloadHome}
        >
          MyApp
        </NavLink>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#mainNavbar"
          aria-controls="mainNavbar"
          aria-expanded="false"
          aria-label={t('nav.toggle')}
        >
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="mainNavbar">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <NavLink
                to="/"
                end
                className={getLinkClass}
                onMouseEnter={preloadHome}
                onFocus={preloadHome}
                aria-current="page"
              >
                {t('nav.home')}
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink
                to="/users"
                className={getLinkClass}
                onMouseEnter={preloadUsers}
                onFocus={preloadUsers}
              >
                {t('nav.users')}
              </NavLink>
            </li>
          </ul>

          <div className="d-flex align-items-center gap-2">
            <div className="dropdown">
              <button
                className="btn btn-outline-light btn-sm dropdown-toggle"
                id="langDropdown"
                data-bs-toggle="dropdown"
                aria-expanded="false"
                aria-label={t('nav.language')}
              >
                {locale.toUpperCase()}
              </button>
              <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="langDropdown">
                <li>
                  <button
                    className="dropdown-item"
                    onClick={() => setLocale('en')}
                    aria-current={locale === 'en' ? 'true' : undefined}
                  >
                    English
                  </button>
                </li>
                <li>
                  <button
                    className="dropdown-item"
                    onClick={() => setLocale('el')}
                    aria-current={locale === 'el' ? 'true' : undefined}
                  >
                    Ελληνικά
                  </button>
                </li>
              </ul>
            </div>

            {auth.isAuthenticated ? (
              <div className="dropdown">
                <button
                  className="btn btn-light btn-sm dropdown-toggle"
                  id="userDropdown"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  <i className="bi bi-person-circle me-1" aria-hidden="true" />
                  <span className="visually-hidden">{t('nav.userMenu')}</span>
                  {auth.user?.profile?.name ?? auth.user?.profile?.preferred_username ?? 'User'}
                </button>
                <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                  <li>
                    <button className="dropdown-item" onClick={() => navigate('/users')}>
                      {t('nav.manageUsers')}
                    </button>
                  </li>
                  <li><hr className="dropdown-divider" /></li>
                  <li>
                    <button className="dropdown-item" onClick={onLogout}>
                      {t('auth.signOut')}
                    </button>
                  </li>
                </ul>
              </div>
            ) : (
              <button className="btn btn-outline-light btn-sm" onClick={onLogin}>
                {t('auth.signIn')}
              </button>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
