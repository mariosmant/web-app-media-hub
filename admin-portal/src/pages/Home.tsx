import { useAuth } from 'react-oidc-context';
import { useI18n } from '../i18n/i18n';

export default function Home() {
  const auth = useAuth();
  const { t } = useI18n();

  return (
    <section className="py-4">
      <h1 className="h3 mb-3">{t('home.title')}</h1>
      {auth.isAuthenticated ? (
        <div className="card shadow-sm">
          <div className="card-body">
            <p className="mb-3">{t('home.welcome')}</p>
            <h2 className="h6">{t('home.profile')}</h2>
            <pre className="bg-light p-3 rounded small" aria-label="OIDC profile JSON">
              {JSON.stringify(
                {
                  subject: auth.user?.profile?.sub,
                  name: auth.user?.profile?.name,
                  email: auth.user?.profile?.email,
                  roles: auth.user?.profile?.realm_access?.roles ?? (auth.user?.profile as any)?.roles, // TODO roles type.
                },
                null,
                2
              )}
            </pre>
          </div>
        </div>
      ) : (
        <p className="text-muted">{t('home.pleaseLogin')}</p>
      )}
    </section>
  );
}
