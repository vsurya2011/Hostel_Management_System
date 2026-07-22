import React, { useState } from 'react';
import { CheckCircle, Plus } from 'lucide-react';
import { Button, Select, Modal, ErrorBanner, Badge } from '../components/ui';
import { DataTable } from '../components/table/DataTable';
import { useQuery, useMutation } from '../hooks/useApi';
import { useToast } from '../context/ToastContext';
import { attendanceService, studentsService } from '../lib/services';
import { useLanguage } from '../context/LanguageContext';

const todayISO = () => new Date().toISOString().slice(0, 10);

export const AttendanceView = () => {
  const { t } = useLanguage();
  const [date, setDate] = useState(todayISO());
  const { data: records, isLoading, error, refetch } = useQuery(() => attendanceService.byDate(date), [date]);
  const { data: students } = useQuery(() => studentsService.getAll(), []);
  const { addToast } = useToast();
  const [markOpen, setMarkOpen] = useState(false);
  const [formError, setFormError] = useState('');

  const markMutation = useMutation((payload) => attendanceService.mark(payload));

  const handleMark = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const payload = {
      studentId: Number(form.get('studentId')),
      date: form.get('date'),
      status: form.get('status'),
    };
    try {
      await markMutation.mutate(payload);
      addToast('Attendance marked!');
      setMarkOpen(false);
      if (payload.date === date) refetch();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to mark attendance', 'error');
    }
  };

  const columns = [
    { header: 'Student', accessor: 'studentName', cell: (r) => <span className="font-semibold text-slate-800">{r.studentName}</span> },
    { header: 'Date', accessor: 'date' },
    { header: 'Status', accessor: 'status', cell: (r) => <Badge status={r.status}>{r.status}</Badge> },
    { header: 'Marked By', accessor: 'markedByName', cell: (r) => <span className="text-slate-500">{r.markedByName || '—'}</span> },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-800 tracking-tight">{t('nav_attendance')}</h2>
          <p className="text-sm text-slate-500 mt-1">Mark and review daily student attendance.</p>
        </div>
        <Button
          icon={Plus}
          onClick={() => {
            setFormError('');
            setMarkOpen(true);
          }}
        >
          Mark Attendance
        </Button>
      </div>

      <ErrorBanner message={error} />

      <div className="flex items-center gap-3">
        <label className="text-sm font-medium text-slate-700">Date</label>
        <input
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          className="px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
        />
      </div>

      <DataTable columns={columns} data={records} isLoading={isLoading} emptyMessage="No attendance records for this date." />

      {markOpen && (
        <Modal title="Mark Attendance" onClose={() => setMarkOpen(false)}>
          <form onSubmit={handleMark} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            <Select label="Student" name="studentId" required defaultValue="">
              <option value="" disabled>
                Select a student…
              </option>
              {students?.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.rollNumber} — {s.name}
                </option>
              ))}
            </Select>
            <div className="space-y-1">
              <label className="block text-sm font-medium text-slate-700">Date</label>
              <input
                type="date"
                name="date"
                required
                defaultValue={date}
                className="w-full px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
              />
            </div>
            <Select label="Status" name="status" required defaultValue="PRESENT">
              <option value="PRESENT">Present</option>
              <option value="ABSENT">Absent</option>
              <option value="LATE">Late</option>
            </Select>
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setMarkOpen(false)}>
                {t('cancel')}
              </Button>
              <Button type="submit" icon={CheckCircle} isLoading={markMutation.isPending}>
                Mark
              </Button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
