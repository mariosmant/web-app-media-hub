import { useEffect, useRef, useState } from 'react';
import { createUser, getUser, updateUser, type User } from '../../api/users';
import { useNavigate, useParams } from 'react-router-dom';
import { ConfirmModal } from '../../components/common/ConfirmModal';
import { useI18n } from '../../i18n/i18n';

type Form = {
  username: string;
  email: string;
  enabled: boolean;
};

export default function Upload() {
  const { id } = useParams<{ id: string }>();
  const isNew = id === 'new' || !id;
  const { t } = useI18n();
  const navigate = useNavigate();
  const [form, setForm] = useState<Form>({ username: '', email: '', enabled: true });
  const [loading, setLoading] = useState(!isNew);
  const [error, setError] = useState<string | null>(null);
  const submitBtnRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (!isNew && id) {
      setLoading(true);
      void getUser(id)
        .then((u) => {
          if (!u) {
            setError(t('user.notFound'));
            return;
          }
          setForm({ username: u.username, email: u.email, enabled: u.enabled });
        })
        .finally(() => setLoading(false));
    }
  }, [id, isNew, t]);

  const onChange = (patch: Partial<Form>) => setForm((f) => ({ ...f, ...patch }));

  const onSubmit = async () => {
    try {
      if (isNew) {
        const created = await createUser(form as Omit<User, 'id'>);
        navigate(`/users/${created.id}`);
      } else {
        await updateUser(id!, form);
        navigate(`/users/${id}`);
      }
    } catch (e) {
      setError(String(e));
    }
  };

  if (loading) {
    return (
      <div className="container py-5" role="status" aria-live="polite">
        <div className="alert alert-info mb-0">Loading…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger" role="alert">
        {error}
      </div>
    );
  }

  return (
    <section className="py-4">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h3 mb-0">{t('user.edit.title')}</h1>
        <div className="btn-group" role="group" aria-label="Actions">
          <button className="btn btn-outline-secondary" onClick={() => navigate(-1)}>
            ←
            <span className="visually-hidden">Back</span>
          </button>
          <button
            className="btn btn-primary"
            data-bs-toggle="modal"
            data-bs-target="#saveUserModal"
            ref={submitBtnRef}
          >
            {t('user.save')}
          </button>
        </div>
      </div>

      <form
        className="card shadow-sm"
        onSubmit={(e) => {
          e.preventDefault();
        }}
        aria-describedby="user-form-help"
      >
        <div className="card-body">
          <div id="user-form-help" className="visually-hidden">
            All fields marked required must be filled.
          </div>

          <div className="row g-3">
            <div className="col-12 col-md-6">
              <label htmlFor="username" className="form-label">
                {t('form.username')}
              </label>
              <input
                id="username"
                className="form-control"
                value={form.username}
                onChange={(e) => onChange({ username: e.target.value })}
                required
                autoComplete="username"
              />
            </div>

            <div className="col-12 col-md-6">
              <label htmlFor="email" className="form-label">
                {t('form.email')}
              </label>
              <input
                id="email"
                type="email"
                className="form-control"
                value={form.email}
                onChange={(e) => onChange({ email: e.target.value })}
                required
                autoComplete="email"
              />
            </div>

            <div className="col-12">
              <label htmlFor="enabled" className="form-label">
                {t('form.status')}
              </label>
              <div className="form-check">
                <input
                  id="enabled"
                  className="form-check-input"
                  type="checkbox"
                  checked={form.enabled}
                  onChange={(e) => onChange({ enabled: e.target.checked })}
                />
                <label className="form-check-label" htmlFor="enabled">
                  {t('form.enabled')}
                </label>
              </div>
            </div>
          </div>
        </div>
      </form>

      <ConfirmModal
        id="saveUserModal"
        title={t('user.confirm.save.title')}
        body={<p className="mb-0">{t('user.confirm.save.body')}</p>}
        confirmText={t('user.save')}
        cancelText={t('user.cancel')}
        onConfirm={onSubmit}
      />
    </section>
  );
}
