import React, { useState } from 'react';
import { Search, Plus, Filter, Edit, Trash2, LogOut } from 'lucide-react';
import { Button, Input, Select, Badge, Modal, ErrorBanner } from '../components/ui';
import { DataTable } from '../components/table/DataTable';
import { useQuery, useMutation } from '../hooks/useApi';
import { useToast } from '../context/ToastContext';
import { useLanguage } from '../context/LanguageContext';
import { studentsService, adminService, roomsService } from '../lib/services';

const emptyForm = {
  userId: '',
  rollNumber: '',
  name: '',
  phone: '',
  department: '',
  year: '',
  guardianName: '',
  guardianPhone: '',
  address: '',
  admissionDate: '',
};

export const StudentsView = () => {
  const { t } = useLanguage();
  const { data: students, isLoading, error, refetch } = useQuery(() => studentsService.getAll(), []);
  const { data: users, refetch: refetchUsers } = useQuery(() => adminService.getAllUsers(), []);
  const { addToast } = useToast();
  const [searchTerm, setSearchTerm] = useState('');
  const [modalState, setModalState] = useState(null); // null | { mode: 'create' | 'edit', student? }
  const [formError, setFormError] = useState('');

  const createMutation = useMutation((payload) => studentsService.create(payload));
  const updateMutation = useMutation(({ id, payload }) => studentsService.update(id, payload));
  const deleteMutation = useMutation((id) => studentsService.delete(id));
  const vacateMutation = useMutation((studentId) => roomsService.vacate(studentId));

  const filtered =
    students?.filter(
      (s) =>
        s.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        s.rollNumber?.toLowerCase().includes(searchTerm.toLowerCase())
    ) || [];

  const unlinkedUsers = users?.filter((u) => !u.hasStudentProfile) || [];

  const openCreate = () => {
    setFormError('');
    setModalState({ mode: 'create' });
  };
  const openEdit = (student) => {
    setFormError('');
    setModalState({ mode: 'edit', student });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const payload = Object.fromEntries(form.entries());
    if (payload.year) payload.year = Number(payload.year);
    else delete payload.year;
    if (payload.userId) payload.userId = Number(payload.userId);
    if (!payload.admissionDate) delete payload.admissionDate;

    try {
      if (modalState.mode === 'create') {
        await createMutation.mutate(payload);
        addToast('Student added successfully!');
      } else {
        await updateMutation.mutate({ id: modalState.student.id, payload });
        addToast('Student updated successfully!');
      }
      setModalState(null);
      refetch();
      refetchUsers();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to save student', 'error');
    }
  };

  const handleDelete = async (student) => {
    if (!window.confirm(`Delete ${student.name}? This cannot be undone.`)) return;
    try {
      await deleteMutation.mutate(student.id);
      addToast('Student deleted');
      refetch();
    } catch (err) {
      addToast(err.message || 'Failed to delete student', 'error');
    }
  };

  const handleVacate = async (student) => {
    if (!window.confirm(`Vacate Room ${student.roomNumber} for ${student.name}? They'll show as unallocated afterwards.`)) return;
    try {
      await vacateMutation.mutate(student.id);
      addToast('Room vacated successfully');
      refetch();
    } catch (err) {
      addToast(err.message || 'Failed to vacate room', 'error');
    }
  };

  const columns = [
    { header: 'Roll No.', accessor: 'rollNumber', cell: (r) => <span className="font-bold text-slate-900">{r.rollNumber}</span> },
    {
      header: 'Name & Contact',
      cell: (r) => (
        <div>
          <p className="font-semibold text-slate-800">{r.name}</p>
          <p className="text-xs text-slate-500 mt-0.5">{r.phone}</p>
        </div>
      ),
    },
    {
      header: 'Room',
      cell: (r) =>
        r.roomNumber ? (
          <span className="inline-flex items-center px-2 py-1 rounded bg-emerald-50 text-emerald-700 text-xs font-semibold border border-emerald-200">
            Allocated — Room {r.roomNumber}
          </span>
        ) : (
          <span className="inline-flex items-center px-2 py-1 rounded bg-slate-100 text-slate-500 text-xs font-semibold border border-slate-200">
            Unassigned
          </span>
        ),
    },
    { header: 'Department', accessor: 'department' },
    { header: 'Status', accessor: 'status', cell: (r) => <Badge status={r.status}>{r.status}</Badge> },
    {
      header: 'Actions',
      cell: (r) => (
        <div className="flex items-center gap-2">
          {r.roomNumber && (
            <button
              onClick={() => handleVacate(r)}
              title="Vacate room"
              className="p-1.5 text-slate-400 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition-colors"
            >
              <LogOut className="w-4 h-4" />
            </button>
          )}
          <button onClick={() => openEdit(r)} className="p-1.5 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
            <Edit className="w-4 h-4" />
          </button>
          <button onClick={() => handleDelete(r)} className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors">
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-800 tracking-tight">{t('nav_students')}</h2>
          <p className="text-sm text-slate-500 mt-1">Manage student records, room allocations, and details.</p>
        </div>
        <Button icon={Plus} onClick={openCreate}>
          {t('add_student')}
        </Button>
      </div>

      <ErrorBanner message={error} />

      <div className="flex flex-col sm:flex-row gap-4 mb-2">
        <div className="relative flex-1 max-w-md group">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 group-focus-within:text-indigo-500" />
          <input
            type="text"
            placeholder="Search by name or roll number..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-9 pr-4 py-2.5 text-sm bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-indigo-50 focus:border-indigo-500 transition-all shadow-sm"
          />
        </div>
        <Button variant="secondary" icon={Filter}>
          {t('filters')}
        </Button>
      </div>

      <DataTable columns={columns} data={filtered} isLoading={isLoading} emptyMessage="No students found matching your search." />

      {modalState && (
        <Modal title={modalState.mode === 'create' ? 'Add New Student' : `Edit ${modalState.student.name}`} onClose={() => setModalState(null)}>
          <form onSubmit={handleSubmit} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            {modalState.mode === 'create' && (
              <Select label="User" name="userId" required defaultValue="">
                <option value="" disabled>
                  Select a registered user…
                </option>
                {unlinkedUsers.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.username} ({u.email}) — id {u.id}
                  </option>
                ))}
              </Select>
            )}
            {modalState.mode === 'create' && unlinkedUsers.length === 0 && (
              <p className="text-xs text-amber-600 -mt-2">
                No unlinked users available — register one first via Auth &gt; Register.
              </p>
            )}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input label="Roll Number" name="rollNumber" required defaultValue={modalState.student?.rollNumber} placeholder="e.g. CS2024001" />
              <Input label="Full Name" name="name" required defaultValue={modalState.student?.name} placeholder="e.g. John Doe" />
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input label="Department" name="department" defaultValue={modalState.student?.department} placeholder="e.g. Computer Science" />
              <Input label="Year" name="year" type="number" min="1" max="6" defaultValue={modalState.student?.year} placeholder="e.g. 2" />
            </div>
            <Input label="Phone Number" name="phone" defaultValue={modalState.student?.phone} placeholder="+91..." />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input label="Guardian Name" name="guardianName" defaultValue={modalState.student?.guardianName} />
              <Input label="Guardian Phone" name="guardianPhone" defaultValue={modalState.student?.guardianPhone} />
            </div>
            <Input label="Address" name="address" defaultValue={modalState.student?.address} />
            <Input label="Admission Date" name="admissionDate" type="date" defaultValue={modalState.student?.admissionDate} />
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setModalState(null)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={createMutation.isPending || updateMutation.isPending}>
                {t('save')}
              </Button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
