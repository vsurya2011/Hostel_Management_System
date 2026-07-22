import React from 'react';
import { AlertCircle } from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';

const SkeletonRow = ({ cols }) => (
  <tr className="animate-pulse border-b border-slate-50">
    {Array.from({ length: cols }).map((_, i) => (
      <td key={i} className="px-6 py-4">
        <div className="h-4 bg-slate-100 rounded w-3/4"></div>
      </td>
    ))}
  </tr>
);

const SkeletonCard = () => (
  <div className="animate-pulse bg-white rounded-xl border border-slate-100 shadow-sm p-4 space-y-3">
    <div className="h-4 bg-slate-100 rounded w-1/2"></div>
    <div className="h-3 bg-slate-100 rounded w-3/4"></div>
    <div className="h-3 bg-slate-100 rounded w-2/3"></div>
  </div>
);

const EmptyBlock = ({ emptyMessage }) => (
  <div className="flex flex-col items-center justify-center py-12 text-slate-400">
    <AlertCircle className="w-8 h-8 mb-2 text-slate-300" />
    <p className="text-sm text-center px-4">{emptyMessage}</p>
  </div>
);

// A single column config (header + accessor/cell) is shared between the
// desktop <table> and the mobile stacked-card layout below, so every page
// using DataTable gets responsive behaviour for free.
export const DataTable = ({ columns, data, isLoading, emptyMessage }) => {
  const { t } = useLanguage();
  const resolvedEmptyMessage = emptyMessage ?? t('no_records');
  // Actions columns look better left-aligned on mobile cards rather than
  // stacked as a "label: value" row, so we detect them by header text.
  const isActionsCol = (col) => /action/i.test(col.header || '');

  return (
    <div className="w-full">
      {/* Desktop / tablet table view */}
      <div className="hidden md:block w-full overflow-x-auto rounded-xl border border-slate-100 bg-white shadow-sm custom-scrollbar">
        <table className="w-full text-sm text-left whitespace-nowrap">
          <thead className="text-xs text-slate-500 uppercase bg-slate-50/80 border-b border-slate-100">
            <tr>
              {columns.map((col, i) => (
                <th key={i} className="px-4 lg:px-6 py-4 font-semibold tracking-wider">
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-50">
            {isLoading ? (
              Array.from({ length: 5 }).map((_, i) => <SkeletonRow key={i} cols={columns.length} />)
            ) : !data || data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-6 py-12 text-center text-slate-400">
                  <div className="flex flex-col items-center justify-center">
                    <AlertCircle className="w-8 h-8 mb-2 text-slate-300" />
                    <p>{resolvedEmptyMessage}</p>
                  </div>
                </td>
              </tr>
            ) : (
              data.map((row, i) => (
                <tr key={row.id ?? i} className="hover:bg-slate-50/50 transition-colors group">
                  {columns.map((col, j) => (
                    <td key={j} className="px-4 lg:px-6 py-4 text-slate-600">
                      {col.cell ? col.cell(row) : row[col.accessor]}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
        {!isLoading && data?.length > 0 && (
          <div className="px-4 lg:px-6 py-3 border-t border-slate-100 bg-slate-50/50">
            <span className="text-xs text-slate-500">
              {t('showing_entries', { count: data.length })}
            </span>
          </div>
        )}
      </div>

      {/* Mobile stacked-card view */}
      <div className="md:hidden">
        {isLoading ? (
          <div className="space-y-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <SkeletonCard key={i} />
            ))}
          </div>
        ) : !data || data.length === 0 ? (
          <div className="bg-white rounded-xl border border-slate-100 shadow-sm">
            <EmptyBlock emptyMessage={resolvedEmptyMessage} />
          </div>
        ) : (
          <div className="space-y-3">
            {data.map((row, i) => {
              const actionCols = columns.filter(isActionsCol);
              const fieldCols = columns.filter((c) => !isActionsCol(c));
              return (
                <div key={row.id ?? i} className="bg-white rounded-xl border border-slate-100 shadow-sm p-4">
                  <div className="space-y-2.5">
                    {fieldCols.map((col, j) => (
                      <div key={j} className="flex items-start justify-between gap-3">
                        <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wide shrink-0 pt-0.5">
                          {col.header}
                        </span>
                        <span className="text-sm text-slate-700 text-right min-w-0">{col.cell ? col.cell(row) : row[col.accessor]}</span>
                      </div>
                    ))}
                  </div>
                  {actionCols.length > 0 && (
                    <div className="mt-3 pt-3 border-t border-slate-100 flex justify-end gap-2">
                      {actionCols.map((col, j) => (
                        <React.Fragment key={j}>{col.cell ? col.cell(row) : row[col.accessor]}</React.Fragment>
                      ))}
                    </div>
                  )}
                </div>
              );
            })}
            <p className="text-xs text-slate-400 text-center pt-1">
              {t('showing_entries', { count: data.length })}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};
