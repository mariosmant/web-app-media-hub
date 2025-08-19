import { useEffect, useMemo, useRef, useState } from 'react';
import { DataTable, type ColumnDef } from '../../components/table/DataTable';
import { useNavigate } from 'react-router-dom';
import { listUsers, deleteUser, type User } from '../../api/users';
import { ConfirmModal } from '../../components/common/ConfirmModal';
import { useI18n } from '../../i18n/i18n';

export default function UsersList() {
  const { t } = useI18n();
  const navigate = useNavigate();
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);
  const deleteBtnRef = useRef<HTMLButtonElement>(null);

  const refresh = async () => {
    setLoading(true);
    try {
      const rows = await listUsers();
      setUsers(rows);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void refresh();
  }, []);

  const columns: ColumnDef<User>[] = useMemo(
    () => [
      { key: 'id', header: t('users.col.id'), width: '80px' },
      { key: 'username', header: t('users.col.username') },
      { key: 'email', header: t('users.col.email') },
      {
        key: 'enabled',
        header: t('users.col.status'),
        render: (u) =>
          u.enabled ? (
            <span className="badge text-bg-success">{t('users.enabled')}</span>
          ) : (
            <span className="badge text-bg-secondary">{t('users.disabled')}</span>
          ),
        width: '140px',
      },
      {
        key: 'actions',
        header: t('users.col.actions'),
        headerSrOnly: false,
        render: (u) => (
          <div className="dropdown">
            <button
              className="btn btn-sm btn-outline-secondary dropdown-toggle"
              id={`actions-${u.id}`}
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              {t('users.col.actions')}
            </button>
            <ul className="dropdown-menu" aria-labelledby={`actions-${u.id}`}>
              <li>
                <button className="dropdown-item" onClick={() => navigate(`/users/${u.id}`)}>
                  {t('users.view')}
                </button>
              </li>
              <li>
                <button className="dropdown-item" onClick={() => navigate(`/users/${u.id}/edit`)}>
                  {t('users.edit')}
                </button>
              </li>
              <li>
                <button
                  className="dropdown-item text-danger"
                  data-bs-toggle="modal"
                  data-bs-target="#deleteUserModal"
                  onClick={() => setDeletingId(u.id)}
                  ref={deleteBtnRef}
                >
                  {t('users.delete')}
                </button>
              </li>
            </ul>
          </div>
        ),
        width: '160px',
      },
    ],
    [navigate, t]
  );

  return (
    <section className="py-4">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h3 mb-0">{t('users.title')}</h1>
        <button
          className="btn btn-primary"
          onClick={() => navigate('/users/new/edit')}
          aria-label={t('users.create')}
        >
          <i className="bi bi-plus-lg me-2" aria-hidden="true" />
          {t('users.create')}
        </button>
      </div>

      <div className="card shadow-sm">
        <div className="card-body">
          {loading ? (
            <div role="status" aria-live="polite" className="text-muted">
              Loading…
            </div>
          ) : (
            <DataTable
              columns={columns}
              data={users}
              caption={t('users.table.caption')}
            />
          )}
        </div>
      </div>

      <ConfirmModal
        id="deleteUserModal"
        title={t('users.confirm.delete.title')}
        body={<p className="mb-0">{t('users.confirm.delete.body')}</p>}
        confirmText={t('users.delete')}
        cancelText={t('user.cancel')}
        onConfirm={async () => {
          if (!deletingId) return;
          await deleteUser(deletingId);
          setDeletingId(null);
          await refresh();
        }}
        onCancel={() => setDeletingId(null)}
      />
    </section>
  );
}
