import React, { useState } from 'react';
import { Loader2 } from 'lucide-react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import { AppLayout } from './components/layout/AppLayout';
import { LoginView } from './pages/Login';
import { DashboardView } from './pages/Dashboard';
import { StudentsView } from './pages/Students';
import { HostelsView } from './pages/Hostels';
import { RoomsView } from './pages/Rooms';
import { AttendanceView } from './pages/Attendance';
import { ComplaintsView } from './pages/Complaints';
import { PaymentsView } from './pages/Payments';
import { WardensView } from './pages/Wardens';

const DEFAULT_VIEW_BY_ROLE = {
  ADMIN: 'dashboard',
  WARDEN: 'dashboard',
  STAFF: 'students',
  STUDENT: 'complaints',
};

function AuthenticatedApp() {
  const { user } = useAuth();
  const [currentView, setCurrentView] = useState(DEFAULT_VIEW_BY_ROLE[user?.role] || 'rooms');

  return (
    <AppLayout currentView={currentView} navigate={setCurrentView}>
      {currentView === 'dashboard' && ['ADMIN', 'WARDEN'].includes(user?.role) && <DashboardView />}
      {currentView === 'students' && <StudentsView />}
      {currentView === 'hostels' && <HostelsView />}
      {currentView === 'wardens' && user?.role === 'ADMIN' && <WardensView />}
      {currentView === 'rooms' && <RoomsView />}
      {currentView === 'attendance' && <AttendanceView />}
      {currentView === 'complaints' && <ComplaintsView />}
      {currentView === 'payments' && <PaymentsView />}
    </AppLayout>
  );
}

function Shell() {
  const { user, isInitializing } = useAuth();

  if (isInitializing) {
    return (
      <div className="h-screen w-screen flex items-center justify-center bg-slate-50 dark:bg-[#0b1220] transition-colors">
        <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
      </div>
    );
  }

  return !user ? <LoginView /> : <AuthenticatedApp />;
}

export default function App() {
  return (
    <ToastProvider>
      <AuthProvider>
        <Shell />
      </AuthProvider>
    </ToastProvider>
  );
}
