import React from 'react';

export type ColumnDef<T> = {
  key: keyof T | string;
  header: string;
  render?: (row: T) => React.ReactNode;
  headerSrOnly?: boolean;
  width?: string;
};

type Props<T> = {
  columns: ColumnDef<T>[];
  data: T[];
  caption?: string;
  onRowClick?: (row: T) => void;
};

export function DataTable<T extends Record<string, unknown>>({
  columns,
  data,
  caption,
  onRowClick,
}: Props<T>) {
  return (
    <div className="table-responsive">
      <table className="table table-hover align-middle">
        {caption && <caption className="text-muted">{caption}</caption>}
        <thead className="table-light">
          <tr>
            {columns.map((c) => (
              <th
                key={String(c.key)}
                scope="col"
                style={c.width ? { width: c.width } : undefined}
              >
                {c.headerSrOnly ? <span className="visually-hidden">{c.header}</span> : c.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="text-center text-muted py-4">
                No data
              </td>
            </tr>
          ) : (
            data.map((row, idx) => (
              <tr
                key={idx}
                role={onRowClick ? 'button' : undefined}
                onClick={onRowClick ? () => onRowClick(row) : undefined}
                tabIndex={onRowClick ? 0 : -1}
                onKeyDown={(e) => {
                  if (!onRowClick) return;
                  if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    onRowClick(row);
                  }
                }}
              >
                {columns.map((c) => (
                  <td key={String(c.key)}>
                    {c.render ? c.render(row) : String(row[c.key as keyof T] ?? '')}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
