import { useEffect, useState } from 'react';
import { getUser, type User } from '../../api/users';
import { useNavigate, useParams } from 'react-router-dom';
import { useI18n } from '../../i18n/i18n';

export default function UserView() {
  const { id } = useParams<{ id: string }>();
  const { t } = useI18n();
  const navigate = useNavigate();
  const [user, setUser] = useState<User | undefined>();

  useEffect(() => {
    if (id) {
      void getUser(id).then(setUser);
    }
  }, [id]);

  if (!id || user === undefined) {
    return (
      <div className="alert alert-warning" role="status" aria-live="polite">
        {t('user.notFound')}
      </div>
    );
  }

  return (
    <section className="py-4">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h3 mb-0">{t('user.view.title')}</h1>
        <div className="btn-group" role="group" aria-label="Actions">
          <button className="btn btn-outline-secondary" onClick={() => navigate(-1)}>
            ←
            <span className="visually-hidden">Back</span>
          </button>
          <button className="btn btn-primary" onClick={() => navigate(`/users/${id}/edit`)}>
            {t('users.edit')}
          </button>
        </div>
      </div>

      <div className="card shadow-sm">
        <div className="card-body">
          <dl className="row">
            <dt className="col-sm-3">{t('users.col.id')}</dt>
            <dd className="col-sm-9">{user.id}</dd>

            <dt className="col-sm-3">{t('users.col.username')}</dt>
            <dd className="col-sm-9">{user.username}</dd>

            <dt className="col-sm-3">{t('users.col.email')}</dt>
            <dd className="col-sm-9">
              <a href={`mailto:${user.email}`}>{user.email}</a>
            </dd>

            <dt className="col-sm-3">{t('users.col.status')}</dt>
            <dd className="col-sm-9">
              {user.enabled ? (
                <span className="badge text-bg-success">{t('users.enabled')}</span>
              ) : (
                <span className="badge text-bg-secondary">{t('users.disabled')}</span>
              )}
            </dd>
          </dl>
        </div>
      </div>
    </section>
  );
}
