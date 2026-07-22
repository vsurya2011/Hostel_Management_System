import React, { useState } from 'react';
import { Building, AlertCircle, Sun, Moon, Languages } from 'lucide-react';
import { Button, Input, ErrorBanner } from '../components/ui';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { useTheme } from '../context/ThemeContext';
import { useLanguage } from '../context/LanguageContext';

export const LoginView = () => {
  const { login, register } = useAuth();
  const { addToast } = useToast();
  const { isDark, toggleTheme } = useTheme();
  const { t, language, setLanguage, languages } = useLanguage();
  const [mode, setMode] = useState('login'); // 'login' | 'register'
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [infoMessage, setInfoMessage] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setInfoMessage('');
    const form = new FormData(e.target);
    try {
      await login(form.get('usernameOrEmail'), form.get('password'));
      addToast('Login successful!');
    } catch (err) {
      setError(err.message || 'Invalid username or password');
      addToast('Authentication failed', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setInfoMessage('');
    const form = new FormData(e.target);
    const payload = Object.fromEntries(form.entries());
    try {
      const result = await register(payload);
      if (result?.pendingApproval) {
        addToast('Registration submitted — awaiting admin approval');
        setMode('login');
        setInfoMessage(
          result.message ||
            'Your warden account was created and is awaiting admin approval. You can sign in once approved.'
        );
      } else {
        addToast('Account created!');
      }
    } catch (err) {
      setError(err.message || 'Registration failed');
      addToast('Registration failed', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden transition-colors">
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden z-0 pointer-events-none">
        <div className="absolute -top-[20%] -right-[10%] w-[50%] h-[50%] rounded-full bg-indigo-100/50 blur-3xl"></div>
        <div className="absolute top-[60%] -left-[10%] w-[40%] h-[40%] rounded-full bg-purple-100/50 blur-3xl"></div>
      </div>

      <div className="absolute top-4 right-4 z-20 flex items-center gap-2">
        <div className="relative group">
          <button
            className="flex items-center gap-1.5 px-3 py-2 bg-white/80 backdrop-blur border border-slate-200 rounded-xl text-xs font-semibold text-slate-600 hover:bg-white transition-colors shadow-sm"
            title={t('language')}
          >
            <Languages className="w-4 h-4" />
            {language.toUpperCase()}
          </button>
          <div className="absolute right-0 mt-1 w-40 bg-white rounded-xl shadow-2xl border border-slate-100 overflow-hidden opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-30">
            {languages.map((l) => (
              <button
                key={l.code}
                onClick={() => setLanguage(l.code)}
                className={`w-full text-left px-4 py-2 text-sm hover:bg-slate-50 transition-colors ${
                  language === l.code ? 'text-indigo-600 font-semibold' : 'text-slate-600'
                }`}
              >
                {l.nativeLabel}
              </button>
            ))}
          </div>
        </div>
        <button
          onClick={toggleTheme}
          className="p-2.5 bg-white/80 backdrop-blur border border-slate-200 rounded-xl text-slate-600 hover:bg-white transition-colors shadow-sm"
          title={t('toggle_theme')}
        >
          {isDark ? <Sun className="w-4 h-4 text-amber-500" /> : <Moon className="w-4 h-4 text-indigo-500" />}
        </button>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md z-10 relative">
        <div className="flex justify-center mb-6">
          <div className="p-3 bg-indigo-600 rounded-2xl shadow-lg shadow-indigo-200">
            <Building className="w-10 h-10 text-white" />
          </div>
        </div>
        <h2 className="text-center text-3xl font-extrabold text-slate-900 tracking-tight">
          {mode === 'login' ? t('welcome_back') : t('create_your_account')}
        </h2>
        <p className="mt-2 text-center text-sm text-slate-600">
          {mode === 'login' ? t('sign_in_subtitle') : t('register_subtitle')}
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md z-10 relative">
        <div className="bg-white/80 backdrop-blur-xl py-8 px-4 shadow-2xl shadow-indigo-100/50 sm:rounded-2xl sm:px-10 border border-white">
          {mode === 'login' ? (
            <form className="space-y-6" onSubmit={handleLogin}>
              <ErrorBanner message={error} />
              {infoMessage && (
                <div className="p-3 bg-indigo-50 border border-indigo-200 rounded-lg text-sm text-indigo-700">
                  {infoMessage}
                </div>
              )}
              <Input label={t('username_or_email')} name="usernameOrEmail" type="text" required placeholder="admin" />
              <Input label={t('password')} name="password" type="password" required placeholder="••••••••" />
              <Button type="submit" className="w-full py-2.5 text-base" isLoading={loading}>
                {t('sign_in')}
              </Button>
              <p className="text-center text-sm text-slate-500">
                {t('new_here')}{' '}
                <button type="button" className="font-semibold text-indigo-600 hover:text-indigo-500" onClick={() => setMode('register')}>
                  {t('create_an_account')}
                </button>
              </p>
            </form>
          ) : (
            <form className="space-y-4" onSubmit={handleRegister}>
              <ErrorBanner message={error} />
              <Input label={t('full_name')} name="fullName" type="text" required placeholder="Jane Doe" />
              <Input label={t('username')} name="username" type="text" required placeholder="jane.doe" minLength={4} />
              <Input label={t('email')} name="email" type="email" required placeholder="jane@example.com" />
              <Input label={t('password')} name="password" type="password" required placeholder="At least 6 characters" minLength={6} />
              <div className="space-y-1">
                <label className="block text-sm font-medium text-slate-700">{t('role')}</label>
                <select name="role" className="w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100">
                  <option value="STUDENT">{t('role_student')}</option>
                  <option value="STAFF">{t('role_staff')}</option>
                  <option value="WARDEN">{t('role_warden')}</option>
                  <option value="ADMIN">{t('role_admin')}</option>
                </select>
                <p className="text-xs text-slate-400">
                  {t('warden_note')}
                </p>
              </div>
              <Button type="submit" className="w-full py-2.5 text-base" isLoading={loading}>
                {t('create_account_btn')}
              </Button>
              <p className="text-center text-sm text-slate-500">
                {t('already_have_account')}{' '}
                <button type="button" className="font-semibold text-indigo-600 hover:text-indigo-500" onClick={() => setMode('login')}>
                  {t('sign_in')}
                </button>
              </p>
            </form>
          )}
        </div>
        {mode === 'login' && (
          <p className="mt-4 text-center text-xs text-slate-400 flex items-center justify-center gap-1">
            <AlertCircle className="w-3 h-3" /> {t('default_account_note')} <code className="mx-1 font-mono">admin</code> /{' '}
            <code className="font-mono">Admin@123</code>
          </p>
        )}
      </div>
    </div>
  );
};
