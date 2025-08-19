import { useEffect, useRef } from 'react';

type Props = {
  id: string;
  title: string;
  body?: string | React.ReactNode;
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => void;
  onCancel?: () => void;
};

/**
 * Accessible confirmation modal using Bootstrap JS for focus management.
 * Trigger via data-bs-toggle="modal" data-bs-target={`#${id}`} or programmatically.
 */
export function ConfirmModal({
  id,
  title,
  body,
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  onConfirm,
  onCancel,
}: Props) {
  const closeBtnRef = useRef<HTMLButtonElement>(null);

  // Ensure focus returns to close button when hidden for keyboard users
  useEffect(() => {
    const modalEl = document.getElementById(id);
    if (!modalEl) return;
    const handler = () => {
      closeBtnRef.current?.focus();
    };
    modalEl.addEventListener('hidden.bs.modal', handler);
    return () => modalEl.removeEventListener('hidden.bs.modal', handler);
  }, [id]);

  return (
    <div className="modal fade" id={id} aria-hidden="true" aria-labelledby={`${id}-label`} tabIndex={-1}>
      <div className="modal-dialog">
        <div className="modal-content">
          <div className="modal-header">
            <h2 id={`${id}-label`} className="modal-title h5">{title}</h2>
            <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" ref={closeBtnRef} />
          </div>
          <div className="modal-body">{body}</div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" data-bs-dismiss="modal" onClick={onCancel}>
              {cancelText}
            </button>
            <button
              type="button"
              className="btn btn-danger"
              data-bs-dismiss="modal"
              onClick={onConfirm}
            >
              {confirmText}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
