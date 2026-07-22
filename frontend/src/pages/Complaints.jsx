import React, { useState } from 'react';
import { Plus, MessageSquare, Send } from 'lucide-react';
import { Button, Input, Select, Textarea, Modal, Badge, ErrorBanner, EmptyState } from '../components/ui';
import { DataTable } from '../components/table/DataTable';
import { useQuery, useMutation } from '../hooks/useApi';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';
import { useMyStudent } from '../hooks/useMyStudent';
import { complaintsService } from '../lib/services';
import { useLanguage } from '../context/LanguageContext';

const STAFF_ROLES = ['ADMIN', 'WARDEN', 'STAFF'];

export const ComplaintsView = () => {
  const { t } = useLanguage();
  const { user } = useAuth();
  const isStaff = STAFF_ROLES.includes(user?.role);
  const { student: myStudent, isLoading: studentLoading } = useMyStudent();

  const {
    data: complaints,
    isLoading,
    error,
    refetch,
  } = useQuery(
    () => (isStaff ? complaintsService.getAll() : myStudent ? complaintsService.byStudent(myStudent.id) : Promise.resolve([])),
    [isStaff, myStudent?.id],
    { enabled: isStaff || !!myStudent }
  );

  const { addToast } = useToast();
  const [createOpen, setCreateOpen] = useState(false);
  const [detailComplaint, setDetailComplaint] = useState(null);
  const [formError, setFormError] = useState('');

  const createMutation = useMutation((payload) => complaintsService.create(myStudent.id, payload));
  const statusMutation = useMutation(({ id, status }) => complaintsService.updateStatus(id, status));
  const replyMutation = useMutation(({ id, message }) => complaintsService.reply(id, message));

  const handleCreate = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const payload = Object.fromEntries(form.entries());
    try {
      await createMutation.mutate(payload);
      addToast('Complaint filed successfully!');
      setCreateOpen(false);
      refetch();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to file complaint', 'error');
    }
  };

  const handleStatusChange = async (complaint, status) => {
    try {
      await statusMutation.mutate({ id: complaint.id, status });
      addToast('Status updated');
      refetch();
      setDetailComplaint((prev) => (prev?.id === complaint.id ? { ...prev, status } : prev));
    } catch (err) {
      addToast(err.message || 'Failed to update status', 'error');
    }
  };

  const handleReply = async (e, complaint) => {
    e.preventDefault();
    const form = new FormData(e.target);
    const message = form.get('message');
    if (!message?.trim()) return;
    try {
      const updated = await replyMutation.mutate({ id: complaint.id, message });
      addToast('Reply sent');
      e.target.reset();
      setDetailComplaint(updated);
      refetch();
    } catch (err) {
      addToast(err.message || 'Failed to send reply', 'error');
    }
  };

  const columns = [
    { header: 'Title', accessor: 'title', cell: (r) => <span className="font-semibold text-slate-800">{r.title}</span> },
    ...(isStaff ? [{ header: 'Student', accessor: 'studentName' }] : []),
    { header: 'Category', accessor: 'category', cell: (r) => <span className="text-slate-500">{r.category || '—'}</span> },
    { header: 'Priority', accessor: 'priority', cell: (r) => <Badge status={r.priority}>{r.priority || 'Normal'}</Badge> },
    { header: 'Status', accessor: 'status', cell: (r) => <Badge status={r.status}>{r.status}</Badge> },
    {
      header: 'Actions',
      cell: (r) => (
        <Button variant="secondary" className="px-3 py-1.5 h-auto text-xs" icon={MessageSquare} onClick={() => setDetailComplaint(r)}>
          View
        </Button>
      ),
    },
  ];

  if (!isStaff && studentLoading) {
    return <div className="text-sm text-slate-500">Loading your student profile…</div>;
  }

  if (!isStaff && !myStudent) {
    return (
      <EmptyState
        title="No linked student profile"
        description="We couldn't find a Student record matching your account email/username. Ask an admin to link your account, or contact the hostel office to file a complaint on your behalf."
        icon={MessageSquare}
      />
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-800 tracking-tight">{t('nav_complaints')}</h2>
          <p className="text-sm text-slate-500 mt-1">{isStaff ? 'Review and resolve student complaints.' : 'File and track your complaints.'}</p>
        </div>
        {!isStaff && (
          <Button
            icon={Plus}
            onClick={() => {
              setFormError('');
              setCreateOpen(true);
            }}
          >
            File Complaint
          </Button>
        )}
      </div>

      <ErrorBanner message={error} />

      <DataTable columns={columns} data={complaints} isLoading={isLoading} emptyMessage="No complaints found." />

      {createOpen && (
        <Modal title="File a Complaint" onClose={() => setCreateOpen(false)}>
          <form onSubmit={handleCreate} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            <Input label="Title" name="title" required placeholder="e.g. Leaking tap in room" />
            <Textarea label="Description" name="description" rows={3} placeholder="Describe the issue…" />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Select label="Category" name="category" defaultValue="MAINTENANCE">
                <option value="MAINTENANCE">Maintenance</option>
                <option value="ELECTRICAL">Electrical</option>
                <option value="PLUMBING">Plumbing</option>
                <option value="FOOD">Food</option>
                <option value="SECURITY">Security</option>
                <option value="OTHER">Other</option>
              </Select>
              <Select label="Priority" name="priority" defaultValue="MEDIUM">
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </Select>
            </div>
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setCreateOpen(false)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={createMutation.isPending}>
                Submit
              </Button>
            </div>
          </form>
        </Modal>
      )}

      {detailComplaint && (
        <Modal title={detailComplaint.title} onClose={() => setDetailComplaint(null)} maxWidth="max-w-lg">
          <div className="p-6 space-y-4">
            <div className="flex items-center gap-2 flex-wrap">
              <Badge status={detailComplaint.status}>{detailComplaint.status}</Badge>
              <Badge status={detailComplaint.priority}>{detailComplaint.priority || 'Normal'}</Badge>
              {detailComplaint.category && <span className="text-xs text-slate-500">{detailComplaint.category}</span>}
            </div>
            <p className="text-sm text-slate-600">{detailComplaint.description || 'No description provided.'}</p>

            {isStaff && (
              <div className="flex items-center gap-2">
                <label className="text-xs font-semibold text-slate-500 uppercase">Update status</label>
                <select
                  value={detailComplaint.status}
                  onChange={(e) => handleStatusChange(detailComplaint, e.target.value)}
                  className="px-2 py-1.5 bg-white border border-slate-200 rounded-lg text-xs focus:outline-none focus:border-indigo-500"
                >
                  <option value="OPEN">Open</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="RESOLVED">Resolved</option>
                  <option value="REJECTED">Rejected</option>
                </select>
              </div>
            )}

            <div className="border-t border-slate-100 pt-4">
              <h4 className="text-sm font-semibold text-slate-700 mb-3">Replies</h4>
              <div className="space-y-3 max-h-52 overflow-y-auto custom-scrollbar">
                {(detailComplaint.replies || []).length === 0 && <p className="text-xs text-slate-400">No replies yet.</p>}
                {(detailComplaint.replies || []).map((reply) => (
                  <div key={reply.id} className="bg-slate-50 rounded-lg p-3">
                    <div className="flex justify-between items-center mb-1">
                      <span className="text-xs font-semibold text-slate-700">{reply.repliedByName}</span>
                      <span className="text-[10px] text-slate-400">{new Date(reply.createdAt).toLocaleString()}</span>
                    </div>
                    <p className="text-sm text-slate-600">{reply.message}</p>
                  </div>
                ))}
              </div>
              <form onSubmit={(e) => handleReply(e, detailComplaint)} className="flex gap-2 mt-3">
                <input
                  name="message"
                  placeholder="Write a reply…"
                  className="flex-1 px-3 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
                />
                <Button type="submit" icon={Send} isLoading={replyMutation.isPending}>
                  Send
                </Button>
              </form>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
