import React from 'react';
import { Users, Building, Home, AlertCircle } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { Card, ErrorBanner } from '../components/ui';
import { useQuery } from '../hooks/useApi';
import { dashboardService } from '../lib/services';
import { useLanguage } from '../context/LanguageContext';

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

const StatCard = ({ title, value, icon: Icon, loading }) => (
  <Card className="p-6 transition-all hover:shadow-md hover:-translate-y-1 duration-300">
    <div className="flex items-center justify-between">
      <div>
        <p className="text-sm font-semibold text-slate-500 mb-1">{title}</p>
        {loading ? (
          <div className="h-8 w-24 bg-slate-100 animate-pulse rounded mt-1"></div>
        ) : (
          <h3 className="text-3xl font-bold text-slate-800 tracking-tight">{value ?? '—'}</h3>
        )}
      </div>
      <div className="p-3 rounded-2xl bg-indigo-50 text-indigo-600">
        <Icon className="w-6 h-6" />
      </div>
    </div>
  </Card>
);

export const DashboardView = () => {
  const { t } = useLanguage();
  const { data, isLoading, error } = useQuery(() => dashboardService.getStats(), []);

  const roomStatus = data
    ? [
        { name: t('occupied'), value: data.occupiedRooms },
        { name: t('available'), value: data.availableRooms },
      ]
    : [];

  const occupancyRate = data && data.totalRooms > 0 ? Math.round((data.occupiedRooms / data.totalRooms) * 100) : 0;

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-800 tracking-tight">{t('dashboard_title')}</h2>
        <p className="text-sm text-slate-500 mt-1">{t('dashboard_subtitle')}</p>
      </div>

      <ErrorBanner message={error} />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard title={t('total_students')} value={data?.totalStudents} icon={Users} loading={isLoading} />
        <StatCard title={t('total_rooms')} value={data?.totalRooms} icon={Building} loading={isLoading} />
        <StatCard title={t('occupied_rooms')} value={data?.occupiedRooms} icon={Home} loading={isLoading} />
        <StatCard title={t('pending_complaints')} value={data?.pendingComplaints} icon={AlertCircle} loading={isLoading} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="lg:col-span-2 p-6">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-lg font-bold text-slate-800">{t('operational_snapshot')}</h3>
          </div>
          <div className="h-64 sm:h-80">
            {isLoading ? (
              <div className="w-full h-full bg-slate-50 animate-pulse rounded-lg"></div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={[
                    { label: 'Students', value: data?.totalStudents || 0 },
                    { label: 'Rooms', value: data?.totalRooms || 0 },
                    { label: 'Complaints', value: data?.pendingComplaints || 0 },
                    { label: 'Leave Req.', value: data?.pendingLeaveRequests || 0 },
                    { label: 'Visitors', value: data?.todayVisitors || 0 },
                  ]}
                  margin={{ top: 5, right: 10, bottom: 5, left: -10 }}
                >
                  <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
                  <XAxis
                    dataKey="label"
                    stroke="#94a3b8"
                    axisLine={false}
                    tickLine={false}
                    dy={10}
                    fontSize={11}
                    interval={0}
                    angle={-20}
                    textAnchor="end"
                    height={40}
                  />
                  <YAxis stroke="#94a3b8" axisLine={false} tickLine={false} fontSize={11} width={30} />
                  <Tooltip contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.1)' }} />
                  <Bar dataKey="value" fill="#4f46e5" radius={[8, 8, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>
        </Card>

        <Card className="p-6">
          <h3 className="text-lg font-bold text-slate-800 mb-6">{t('room_allocation')}</h3>
          <div className="h-56 sm:h-64 relative">
            {isLoading ? (
              <div className="w-48 h-48 mx-auto bg-slate-50 animate-pulse rounded-full"></div>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={roomStatus} cx="50%" cy="50%" innerRadius={70} outerRadius={90} paddingAngle={8} dataKey="value" stroke="none">
                    {roomStatus.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }} />
                </PieChart>
              </ResponsiveContainer>
            )}
            {!isLoading && (
              <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                <span className="text-3xl font-bold text-slate-800">{occupancyRate}%</span>
                <span className="text-xs font-medium text-slate-500 uppercase tracking-wide">{t('occupied')}</span>
              </div>
            )}
          </div>
          <div className="flex flex-col gap-3 mt-6">
            {!isLoading &&
              roomStatus.map((entry, index) => (
                <div key={index} className="flex items-center justify-between text-sm">
                  <div className="flex items-center text-slate-600 font-medium">
                    <span className="w-3 h-3 rounded-full mr-3" style={{ backgroundColor: COLORS[index % COLORS.length] }}></span>
                    {entry.name}
                  </div>
                  <span className="font-semibold text-slate-900">{entry.value}</span>
                </div>
              ))}
            <div className="flex items-center justify-between text-sm pt-3 border-t border-slate-100">
              <span className="text-slate-600 font-medium">{t('total_revenue')}</span>
              <span className="font-semibold text-slate-900">
                {data?.totalRevenue != null ? `₹${Number(data.totalRevenue).toLocaleString()}` : '—'}
              </span>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};
