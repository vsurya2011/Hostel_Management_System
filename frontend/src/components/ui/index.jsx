import React from 'react';
import { Loader2 } from 'lucide-react';

export const Card = ({ children, className = '' }) => (
  <div className={`bg-white rounded-xl shadow-[0_2px_10px_-3px_rgba(6,81,237,0.1)] dark:shadow-none border border-slate-100 overflow-hidden transition-colors ${className}`}>
    {children}
  </div>
);

export const Button = ({
  children,
  variant = 'primary',
  icon: Icon,
  className = '',
  onClick,
  disabled,
  type = 'button',
  isLoading,
}) => {
  const baseStyle =
    'inline-flex items-center justify-center px-4 py-2 text-sm font-semibold rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-60 disabled:cursor-not-allowed active:scale-[0.98]';
  const variants = {
    primary: 'bg-indigo-600 text-white hover:bg-indigo-700 focus:ring-indigo-500 shadow-sm shadow-indigo-200',
    secondary: 'bg-white text-slate-700 border border-slate-200 hover:bg-slate-50 hover:border-slate-300 focus:ring-slate-500 shadow-sm',
    danger: 'bg-rose-600 text-white hover:bg-rose-700 focus:ring-rose-500 shadow-sm dark:shadow-none shadow-rose-200',
    ghost: 'bg-transparent text-slate-600 hover:bg-slate-100 hover:text-slate-900',
  };

  return (
    <button type={type} onClick={onClick} disabled={disabled || isLoading} className={`${baseStyle} ${variants[variant]} ${className}`}>
      {isLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : Icon && <Icon className="w-4 h-4 mr-2" />}
      {children}
    </button>
  );
};

export const Input = React.forwardRef(({ label, error, className = '', ...props }, ref) => (
  <div className={`space-y-1 ${className}`}>
    {label && <label className="block text-sm font-medium text-slate-700">{label}</label>}
    <input
      ref={ref}
      className={`w-full px-3 py-2 bg-white border ${
        error ? 'border-rose-300 focus:ring-rose-200' : 'border-slate-200 focus:ring-indigo-100'
      } rounded-lg text-sm placeholder-slate-400 focus:outline-none focus:border-indigo-500 focus:ring-4 transition-all duration-200`}
      {...props}
    />
    {error && <p className="text-xs text-rose-500 mt-1">{error}</p>}
  </div>
));
Input.displayName = 'Input';

export const Select = React.forwardRef(({ label, error, className = '', children, ...props }, ref) => (
  <div className={`space-y-1 ${className}`}>
    {label && <label className="block text-sm font-medium text-slate-700">{label}</label>}
    <select
      ref={ref}
      className={`w-full px-3 py-2 bg-white border ${
        error ? 'border-rose-300 focus:ring-rose-200' : 'border-slate-200 focus:ring-indigo-100'
      } rounded-lg text-sm text-slate-700 focus:outline-none focus:border-indigo-500 focus:ring-4 transition-all duration-200`}
      {...props}
    >
      {children}
    </select>
    {error && <p className="text-xs text-rose-500 mt-1">{error}</p>}
  </div>
));
Select.displayName = 'Select';

export const Textarea = React.forwardRef(({ label, error, className = '', ...props }, ref) => (
  <div className={`space-y-1 ${className}`}>
    {label && <label className="block text-sm font-medium text-slate-700">{label}</label>}
    <textarea
      ref={ref}
      className={`w-full px-3 py-2 bg-white border ${
        error ? 'border-rose-300 focus:ring-rose-200' : 'border-slate-200 focus:ring-indigo-100'
      } rounded-lg text-sm placeholder-slate-400 focus:outline-none focus:border-indigo-500 focus:ring-4 transition-all duration-200`}
      {...props}
    />
    {error && <p className="text-xs text-rose-500 mt-1">{error}</p>}
  </div>
));
Textarea.displayName = 'Textarea';

const STATUS_COLORS = {
  success: 'bg-emerald-100 text-emerald-700 border-emerald-200',
  warning: 'bg-amber-100 text-amber-700 border-amber-200',
  danger: 'bg-rose-100 text-rose-700 border-rose-200',
  info: 'bg-blue-100 text-blue-700 border-blue-200',
  neutral: 'bg-slate-100 text-slate-700 border-slate-200',
};

export const Badge = ({ children, status }) => {
  let mappedStyle = STATUS_COLORS.neutral;
  const s = status?.toLowerCase() || '';
  if (['active', 'occupied', 'resolved', 'approved', 'present', 'paid', 'success'].includes(s)) mappedStyle = STATUS_COLORS.success;
  if (['pending', 'in_progress', 'in progress', 'partially occupied', 'on leave', 'open'].includes(s)) mappedStyle = STATUS_COLORS.warning;
  if (['maintenance', 'rejected', 'absent', 'failed', 'closed'].includes(s)) mappedStyle = STATUS_COLORS.danger;
  if (['available', 'late'].includes(s)) mappedStyle = STATUS_COLORS.info;

  return <span className={`inline-flex items-center px-2.5 py-1 rounded-md text-xs font-semibold border ${mappedStyle}`}>{children}</span>;
};

export const Modal = ({ title, onClose, children, maxWidth = 'max-w-md' }) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm animate-in fade-in duration-200">
    <div className={`bg-white rounded-2xl shadow-2xl w-full ${maxWidth} overflow-hidden animate-in zoom-in-95 duration-200 max-h-[90vh] flex flex-col`}>
      <div className="px-6 py-4 border-b border-slate-100 flex justify-between items-center bg-slate-50/50 shrink-0">
        <h3 className="text-lg font-bold text-slate-800">{title}</h3>
        <button onClick={onClose} className="text-slate-400 hover:text-slate-600">
          ✕
        </button>
      </div>
      <div className="overflow-y-auto custom-scrollbar">{children}</div>
    </div>
  </div>
);

export const ErrorBanner = ({ message }) =>
  message ? (
    <div className="p-3 bg-rose-50 border border-rose-200 rounded-lg text-sm text-rose-600">{message}</div>
  ) : null;

export const EmptyState = ({ title, description, icon: Icon }) => (
  <div className="h-[40vh] flex flex-col items-center justify-center border-2 border-dashed border-slate-200 rounded-2xl bg-slate-50/50">
    <div className="p-4 bg-white rounded-full shadow-sm mb-4">{Icon && <Icon className="w-8 h-8 text-indigo-300" />}</div>
    <h3 className="text-xl font-bold text-slate-800">{title}</h3>
    {description && <p className="text-slate-500 mt-2 max-w-sm text-center">{description}</p>}
  </div>
);
