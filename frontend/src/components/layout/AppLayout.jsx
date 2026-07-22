import React, { useEffect, useRef, useState } from 'react';
import { Menu, Home, Users, Building, Building2, AlertCircle, CheckCircle, CreditCard, LogOut, Bell, Search, Check, UserCog, Sun, Moon, Languages } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useTheme } from '../../context/ThemeContext';
import { useLanguage } from '../../context/LanguageContext';
import { useQuery, useMutation } from '../../hooks/useApi';
import { notificationsService } from '../../lib/services';

const NAVIGATION = [
  { id: 'dashboard', labelKey: 'nav_dashboard', icon: Home, roles: ['ADMIN', 'WARDEN'] },
  { id: 'students', labelKey: 'nav_students', icon: Users, roles: ['ADMIN', 'WARDEN', 'STAFF'] },
  { id: 'hostels', labelKey: 'nav_hostels', icon: Building2, roles: ['ADMIN', 'WARDEN'] },
  { id: 'wardens', labelKey: 'nav_wardens', icon: UserCog, roles: ['ADMIN'] },
  { id: 'rooms', labelKey: 'nav_rooms', icon: Building, roles: ['ADMIN', 'WARDEN', 'STAFF', 'STUDENT'] },
  { id: 'attendance', labelKey: 'nav_attendance', icon: CheckCircle, roles: ['ADMIN', 'WARDEN', 'STAFF'] },
  { id: 'complaints', labelKey: 'nav_complaints', icon: AlertCircle, roles: ['ADMIN', 'WARDEN', 'STAFF', 'STUDENT'] },
  { id: 'payments', labelKey: 'nav_payments', icon: CreditCard, roles: ['ADMIN', 'WARDEN', 'STUDENT'] },
];

const ThemeToggle = () => {
  const { isDark, toggleTheme } = useTheme();
  const { t } = useLanguage();
  return (
    <button
      onClick={toggleTheme}
      title={t('toggle_theme')}
      aria-label={t('toggle_theme')}
      className="relative p-2.5 text-slate-500 dark:text-slate-300 hover:bg-slate-100 rounded-xl transition-colors"
    >
      <span className="relative block w-5 h-5">
        <Sun
          className={`w-5 h-5 absolute inset-0 transition-all duration-300 text-amber-500 ${
            isDark ? 'opacity-0 -rotate-90 scale-50' : 'opacity-100 rotate-0 scale-100'
          }`}
        />
        <Moon
          className={`w-5 h-5 absolute inset-0 transition-all duration-300 text-indigo-400 ${
            isDark ? 'opacity-100 rotate-0 scale-100' : 'opacity-0 rotate-90 scale-50'
          }`}
        />
      </span>
    </button>
  );
};

const LanguageSwitcher = () => {
  const { language, setLanguage, languages, t } = useLanguage();
  const [open, setOpen] = useState(false);
  const containerRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div className="relative" ref={containerRef}>
      <button
        onClick={() => setOpen((prev) => !prev)}
        title={t('language')}
        aria-label={t('language')}
        className="flex items-center gap-1.5 p-2.5 text-slate-500 dark:text-slate-300 hover:bg-slate-100 rounded-xl transition-colors"
      >
        <Languages className="w-5 h-5" />
        <span className="hidden sm:inline text-xs font-semibold uppercase">{language}</span>
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-44 bg-white rounded-2xl shadow-2xl border border-slate-100 overflow-hidden z-50 animate-fade-in">
          <div className="px-4 py-2.5 border-b border-slate-100">
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wide">{t('language')}</p>
          </div>
          {languages.map((l) => (
            <button
              key={l.code}
              onClick={() => {
                setLanguage(l.code);
                setOpen(false);
              }}
              className={`w-full text-left px-4 py-2.5 text-sm flex items-center justify-between hover:bg-slate-50 transition-colors ${
                language === l.code ? 'text-indigo-600 font-semibold bg-indigo-50/40' : 'text-slate-600'
              }`}
            >
              <span>{l.nativeLabel}</span>
              {language === l.code && <Check className="w-3.5 h-3.5" />}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

const timeAgo = (dateString) => {
  if (!dateString) return '';
  const diffMs = Date.now() - new Date(dateString).getTime();
  const mins = Math.floor(diffMs / 60000);
  if (mins < 1) return 'just now';
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
};

// Some serialized boolean fields in this API lose their "is" prefix over
// the wire (isRead -> read) depending on the bean naming convention, so we
// check both to stay correct either way.
const isUnread = (n) => !(n.read ?? n.isRead);

const NotificationBell = () => {
  const { user } = useAuth();
  const { t } = useLanguage();
  const [open, setOpen] = useState(false);
  const containerRef = useRef(null);

  const { data: notifications, refetch } = useQuery(
    () => notificationsService.byUser(user.id),
    [user?.id],
    { enabled: !!user?.id }
  );
  const markReadMutation = useMutation((id) => notificationsService.markAsRead(id));

  const list = notifications || [];
  const unreadCount = list.filter(isUnread).length;

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleOpen = () => setOpen((prev) => !prev);

  const handleItemClick = async (n) => {
    if (!isUnread(n)) return;
    try {
      await markReadMutation.mutate(n.id);
      refetch();
    } catch {
      // Non-critical — the notification just stays marked unread until retried.
    }
  };

  const handleMarkAllRead = async () => {
    const unread = list.filter(isUnread);
    try {
      await Promise.all(unread.map((n) => notificationsService.markAsRead(n.id)));
      refetch();
    } catch {
      // Non-critical — leaving any that failed as unread is fine.
    }
  };

  return (
    <div className="relative" ref={containerRef}>
      <button
        onClick={handleOpen}
        className="relative p-2.5 text-slate-500 hover:bg-slate-100 rounded-xl transition-colors"
      >
        <Bell className="w-5 h-5" />
        {unreadCount > 0 && (
          <span className="absolute top-2 right-2 w-2 h-2 bg-rose-500 rounded-full border-2 border-white"></span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-80 max-h-96 bg-white rounded-2xl shadow-2xl border border-slate-100 overflow-hidden flex flex-col z-50">
          <div className="px-4 py-3 border-b border-slate-100 flex items-center justify-between shrink-0">
            <h4 className="font-bold text-slate-800 text-sm">{t('notifications')}</h4>
            {unreadCount > 0 && (
              <button onClick={handleMarkAllRead} className="text-xs font-semibold text-indigo-600 hover:text-indigo-800">
                {t('mark_all_read')}
              </button>
            )}
          </div>
          <div className="overflow-y-auto custom-scrollbar">
            {list.length === 0 ? (
              <p className="text-sm text-slate-400 text-center py-8">{t('no_notifications')}</p>
            ) : (
              list.map((n) => (
                <button
                  key={n.id}
                  onClick={() => handleItemClick(n)}
                  className={`w-full text-left px-4 py-3 border-b border-slate-50 last:border-0 hover:bg-slate-50 transition-colors flex gap-2 ${
                    isUnread(n) ? 'bg-indigo-50/40' : ''
                  }`}
                >
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-semibold text-slate-800 truncate">{n.title}</p>
                    {n.message && <p className="text-xs text-slate-500 mt-0.5 line-clamp-2">{n.message}</p>}
                    <p className="text-[11px] text-slate-400 mt-1">{timeAgo(n.createdAt)}</p>
                  </div>
                  {isUnread(n) ? (
                    <span className="w-2 h-2 mt-1.5 rounded-full bg-indigo-500 shrink-0"></span>
                  ) : (
                    <Check className="w-3.5 h-3.5 mt-1 text-slate-300 shrink-0" />
                  )}
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export const AppLayout = ({ children, currentView, navigate }) => {
  const { user, logout } = useAuth();
  const { t } = useLanguage();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [isMobile, setIsMobile] = useState(false);

  useEffect(() => {
    const checkMobile = () => {
      setIsMobile(window.innerWidth < 1024);
      setSidebarOpen(window.innerWidth >= 1024);
    };
    checkMobile();
    window.addEventListener('resize', checkMobile);
    return () => window.removeEventListener('resize', checkMobile);
  }, []);

  const navItems = NAVIGATION.filter((item) => item.roles.includes(user?.role));

  return (
    <div className="flex h-screen bg-[#F8FAFC] font-sans text-slate-900 overflow-hidden">
      {isMobile && sidebarOpen && (
        <div className="fixed inset-0 bg-slate-900/40 z-40 backdrop-blur-sm" onClick={() => setSidebarOpen(false)} />
      )}

      <aside
        className={`fixed lg:static inset-y-0 left-0 z-50 w-64 bg-white border-r border-slate-100 transform transition-transform duration-300 ease-in-out flex flex-col shadow-[4px_0_24px_rgba(0,0,0,0.02)]
        ${sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0 lg:w-20'}`}
      >
        <div className="h-16 flex items-center px-5 border-b border-slate-100 shrink-0">
          <div className="flex items-center gap-3 text-indigo-600 w-full overflow-hidden">
            <div className="p-1.5 bg-indigo-50 rounded-lg shrink-0">
              <Building className="w-6 h-6 shrink-0" />
            </div>
            <span
              className={`font-bold text-lg whitespace-nowrap tracking-tight transition-opacity duration-300 ${
                !sidebarOpen && !isMobile ? 'lg:opacity-0 lg:w-0' : 'opacity-100'
              }`}
            >
              {t('app_name')}<span className="text-slate-800">.</span>
            </span>
          </div>
        </div>

        <nav className="flex-1 overflow-y-auto py-6 px-3 space-y-1 custom-scrollbar">
          <p
            className={`px-3 text-xs font-semibold text-slate-400 uppercase tracking-wider mb-4 transition-opacity ${
              !sidebarOpen && !isMobile ? 'lg:opacity-0' : ''
            }`}
          >
            {t('nav_menu')}
          </p>
          {navItems.map((item) => {
            const isActive = currentView === item.id;
            const label = t(item.labelKey);
            return (
              <button
                key={item.id}
                onClick={() => {
                  navigate(item.id);
                  if (isMobile) setSidebarOpen(false);
                }}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all duration-200 group relative
                  ${isActive ? 'bg-indigo-50 text-indigo-700 font-semibold' : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900 font-medium'}`}
                title={!sidebarOpen && !isMobile ? label : undefined}
              >
                {isActive && <div className="absolute left-0 w-1 h-6 bg-indigo-600 rounded-r-full"></div>}
                <item.icon className={`w-5 h-5 shrink-0 transition-colors ${isActive ? 'text-indigo-600' : 'text-slate-400 group-hover:text-slate-600'}`} />
                <span className={`whitespace-nowrap transition-opacity duration-300 ${!sidebarOpen && !isMobile ? 'lg:hidden' : 'block'}`}>
                  {label}
                </span>
              </button>
            );
          })}
        </nav>

        <div className="p-4 border-t border-slate-100 shrink-0 bg-slate-50/50">
          <div className={`flex items-center gap-3 ${!sidebarOpen && !isMobile ? 'justify-center' : ''}`}>
            <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center text-white font-bold shrink-0 shadow-sm">
              {user?.name?.charAt(0)?.toUpperCase()}
            </div>
            <div className={`text-left overflow-hidden transition-opacity duration-300 ${!sidebarOpen && !isMobile ? 'lg:hidden' : 'block'}`}>
              <p className="text-sm font-semibold text-slate-800 truncate">{user?.name}</p>
              <p className="text-xs text-slate-500 truncate">{user?.role}</p>
            </div>
          </div>
          <button
            onClick={logout}
            className={`mt-4 w-full flex items-center gap-2 text-sm text-slate-500 hover:text-rose-600 transition-colors ${
              !sidebarOpen && !isMobile ? 'justify-center' : 'px-2'
            }`}
          >
            <LogOut className="w-4 h-4" />
            <span className={!sidebarOpen && !isMobile ? 'hidden' : 'block'}>{t('sign_out')}</span>
          </button>
        </div>
      </aside>

      <div className="flex-1 flex flex-col min-w-0 h-screen overflow-hidden">
        <header className="h-16 bg-white/80 backdrop-blur-md border-b border-slate-100 flex items-center justify-between px-4 sm:px-8 shrink-0 z-10 sticky top-0">
          <div className="flex items-center gap-4">
            <button onClick={() => setSidebarOpen(!sidebarOpen)} className="p-2 -ml-2 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors">
              <Menu className="w-5 h-5" />
            </button>
            <h1 className="text-xl font-bold text-slate-800 hidden sm:block capitalize tracking-tight">
              {t(`nav_${currentView.replace(/-/g, '_')}`) !== `nav_${currentView.replace(/-/g, '_')}`
                ? t(`nav_${currentView.replace(/-/g, '_')}`)
                : currentView.replace('-', ' ')}
            </h1>
          </div>

          <div className="flex items-center gap-1 sm:gap-2">
            <div className="relative hidden md:block group mr-2">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 group-focus-within:text-indigo-500 transition-colors" />
              <input
                type="text"
                placeholder={t('search_placeholder')}
                className="w-72 pl-10 pr-4 py-2 bg-slate-100/50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-50 transition-all outline-none"
              />
            </div>
            <LanguageSwitcher />
            <ThemeToggle />
            <NotificationBell />
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-4 sm:p-8 custom-scrollbar">
          <div className="max-w-7xl mx-auto w-full">{children}</div>
        </main>
      </div>
    </div>
  );
};
