import React, { useState } from 'react';
import { Plus, Building, CheckCircle, AlertCircle, UserCheck } from 'lucide-react';
import { Button, Input, Badge, Modal, ErrorBanner, Card, Select } from '../components/ui';
import { DataTable } from '../components/table/DataTable';
import { useQuery, useMutation } from '../hooks/useApi';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';
import { roomsService, studentsService, floorsService } from '../lib/services';
import { useLanguage } from '../context/LanguageContext';

export const RoomsView = () => {
  const { t } = useLanguage();
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const { data: rooms, isLoading, error, refetch } = useQuery(() => roomsService.getAll(), []);
  const { data: students } = useQuery(() => studentsService.getAll(), []);
  const { data: floors } = useQuery(() => floorsService.getAll(), []);
  const { addToast } = useToast();
  const [createOpen, setCreateOpen] = useState(false);
  const [allocateFor, setAllocateFor] = useState(null); // room being allocated to
  const [formError, setFormError] = useState('');

  const createMutation = useMutation((payload) => roomsService.create(payload));
  const allocateMutation = useMutation(({ studentId, roomId }) => roomsService.allocate(studentId, roomId));

  const totalCapacity = rooms?.reduce((sum, r) => sum + (r.capacity || 0), 0) || 0;
  const totalAvailable = rooms?.reduce((sum, r) => sum + Math.max((r.capacity || 0) - (r.occupied || 0), 0), 0) || 0;
  const maintenanceCount = rooms?.filter((r) => r.status?.toLowerCase() === 'maintenance').length || 0;

  const handleCreate = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const payload = Object.fromEntries(form.entries());
    payload.floorId = Number(payload.floorId);
    if (payload.capacity) payload.capacity = Number(payload.capacity);
    if (payload.rentAmount) payload.rentAmount = Number(payload.rentAmount);
    try {
      await createMutation.mutate(payload);
      addToast('Room created successfully!');
      setCreateOpen(false);
      refetch();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to create room', 'error');
    }
  };

  const handleAllocate = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const studentId = Number(form.get('studentId'));
    try {
      await allocateMutation.mutate({ studentId, roomId: allocateFor.id });
      addToast('Room allocated successfully!');
      setAllocateFor(null);
      refetch();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to allocate room', 'error');
    }
  };

  const columns = [
    { header: 'Room No.', accessor: 'roomNumber', cell: (r) => <span className="font-bold text-slate-900">{r.roomNumber}</span> },
    { header: 'Floor', accessor: 'floorInfo', cell: (r) => <span className="text-slate-600">{r.floorInfo || '—'}</span> },
    { header: 'Type', accessor: 'roomType', cell: (r) => <span className="font-medium text-slate-700">{r.roomType || '—'}</span> },
    {
      header: 'Occupancy',
      cell: (r) => (
        <div className="flex items-center gap-2">
          <div className="w-16 h-2 bg-slate-100 rounded-full overflow-hidden">
            <div
              className={`h-full ${r.occupied === r.capacity ? 'bg-rose-500' : 'bg-emerald-500'}`}
              style={{ width: `${r.capacity ? (r.occupied / r.capacity) * 100 : 0}%` }}
            ></div>
          </div>
          <span className="text-xs font-semibold text-slate-600">
            {r.occupied}/{r.capacity}
          </span>
        </div>
      ),
    },
    { header: 'Rent', accessor: 'rentAmount', cell: (r) => <span>{r.rentAmount != null ? `₹${Number(r.rentAmount).toLocaleString()}` : '—'}</span> },
    { header: 'Status', accessor: 'status', cell: (r) => <Badge status={r.status}>{r.status}</Badge> },
    {
      header: 'Actions',
      cell: (r) => {
        const isFull = (r.occupied || 0) >= (r.capacity || 0);
        return (
          <Button
            variant="secondary"
            className="px-3 py-1.5 h-auto text-xs"
            icon={UserCheck}
            disabled={isFull}
            onClick={() => {
              setFormError('');
              setAllocateFor(r);
            }}
          >
            {isFull ? 'Room Full' : 'Allocate'}
          </Button>
        );
      },
    },
  ];

  // A student who already has an active room allocation shows up with a
  // roomNumber on their record (see StudentServiceImpl#toResponse). We use
  // that to clearly mark/disable them in the allocation dropdown instead of
  // silently letting the request fail on the backend.
  const isStudentAllocated = (s) => Boolean(s.roomNumber);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-800 tracking-tight">{t('nav_rooms')}</h2>
          <p className="text-sm text-slate-500 mt-1">Track room availability, allocation, and maintenance status.</p>
        </div>
        {isAdmin && (
          <Button icon={Plus} onClick={() => { setFormError(''); setCreateOpen(true); }}>
            Add Room
          </Button>
        )}
      </div>

      <ErrorBanner message={error} />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="p-5 border-l-4 border-l-indigo-500 flex items-center shadow-sm">
          <div className="p-3 bg-indigo-50 rounded-xl mr-4 text-indigo-600">
            <Building className="w-6 h-6" />
          </div>
          <div>
            <p className="text-sm font-semibold text-slate-500 mb-0.5">Total Capacity</p>
            <p className="text-2xl font-bold text-slate-900">{totalCapacity} Beds</p>
          </div>
        </Card>
        <Card className="p-5 border-l-4 border-l-emerald-500 flex items-center shadow-sm">
          <div className="p-3 bg-emerald-50 rounded-xl mr-4 text-emerald-600">
            <CheckCircle className="w-6 h-6" />
          </div>
          <div>
            <p className="text-sm font-semibold text-slate-500 mb-0.5">Available Beds</p>
            <p className="text-2xl font-bold text-slate-900">{totalAvailable}</p>
          </div>
        </Card>
        <Card className="p-5 border-l-4 border-l-amber-500 flex items-center shadow-sm">
          <div className="p-3 bg-amber-50 rounded-xl mr-4 text-amber-600">
            <AlertCircle className="w-6 h-6" />
          </div>
          <div>
            <p className="text-sm font-semibold text-slate-500 mb-0.5">Under Maintenance</p>
            <p className="text-2xl font-bold text-slate-900">{maintenanceCount} Rooms</p>
          </div>
        </Card>
      </div>

      <DataTable columns={columns} data={rooms} isLoading={isLoading} />

      {createOpen && (
        <Modal title="Add New Room" onClose={() => setCreateOpen(false)}>
          <form onSubmit={handleCreate} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            <Input label="Room Number" name="roomNumber" required placeholder="e.g. A-104" />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Select label="Floor" name="floorId" required defaultValue="">
                <option value="" disabled>
                  Select a floor…
                </option>
                {floors?.map((f) => (
                  <option key={f.id} value={f.id}>
                    {f.hostelName} — {f.blockName} — Floor {f.floorNumber}
                  </option>
                ))}
              </Select>
              <Input label="Capacity" name="capacity" type="number" min="1" placeholder="e.g. 2" />
            </div>
            {(!floors || floors.length === 0) && (
              <p className="text-xs text-amber-600 -mt-2">
                No floors yet — go to Hostels to create a Hostel → Block → Floor first.
              </p>
            )}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input label="Room Type" name="roomType" placeholder="e.g. Double" />
              <Input label="Rent Amount" name="rentAmount" type="number" step="0.01" placeholder="e.g. 8000" />
            </div>
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setCreateOpen(false)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={createMutation.isPending}>
                Save Room
              </Button>
            </div>
          </form>
        </Modal>
      )}

      {allocateFor && (
        <Modal title={`Allocate Room ${allocateFor.roomNumber}`} onClose={() => setAllocateFor(null)}>
          <form onSubmit={handleAllocate} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            <div className="flex items-center justify-between text-xs font-medium text-slate-500 bg-slate-50 border border-slate-100 rounded-lg px-3 py-2">
              <span>Capacity</span>
              <span className="text-slate-700 font-semibold">
                {allocateFor.occupied}/{allocateFor.capacity} beds occupied
              </span>
            </div>
            <Select label="Student" name="studentId" required defaultValue="">
              <option value="" disabled>
                Select a student…
              </option>
              {students?.map((s) => (
                <option key={s.id} value={s.id} disabled={isStudentAllocated(s)}>
                  {s.rollNumber} — {s.name}
                  {isStudentAllocated(s) ? ` (Already Allocated — Room ${s.roomNumber})` : ''}
                </option>
              ))}
            </Select>
            <p className="text-xs text-slate-400 -mt-2">
              Students already assigned to a room are marked “Already Allocated” and can’t be selected again — vacate
              them first from the Students page if you need to move them.
            </p>
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setAllocateFor(null)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={allocateMutation.isPending}>
                Allocate
              </Button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
