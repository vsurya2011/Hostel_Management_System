import React, { useState } from 'react';
import { ShieldCheck, ShieldX, UserCog, Building2, Clock } from 'lucide-react';
import { Button, Select, Badge, ErrorBanner, Card, EmptyState } from '../components/ui';
import { useQuery, useMutation } from '../hooks/useApi';
import { useToast } from '../context/ToastContext';
import { wardenService, hostelsService } from '../lib/services';
import { useLanguage } from '../context/LanguageContext';

// Admin-only. Covers the full warden lifecycle:
//   1. Warden self-registers -> account created disabled, admins notified (backend).
//   2. Admin permits (approves) or rejects the request here.
//   3. Once approved, the warden can sign in and gets attendance/room/complaint
//      access automatically (role-based). Admin then assigns them to a hostel here.
export const WardensView = () => {
  const { t } = useLanguage();
  const { addToast } = useToast();
  const {
    data: pending,
    isLoading: pendingLoading,
    error: pendingError,
    refetch: refetchPending,
  } = useQuery(() => wardenService.getPending(), []);
  const {
    data: approved,
    isLoading: approvedLoading,
    error: approvedError,
    refetch: refetchApproved,
  } = useQuery(() => wardenService.getApproved(), []);
  const { data: hostels } = useQuery(() => hostelsService.getAll(), []);

  const [assigningStaffId, setAssigningStaffId] = useState(null);

  const approveMutation = useMutation((userId) => wardenService.approve(userId));
  const rejectMutation = useMutation((userId) => wardenService.reject(userId));
  const assignMutation = useMutation(({ hostelId, staffId }) => hostelsService.assignWarden(hostelId, staffId));
  const unassignMutation = useMutation((hostelId) => hostelsService.unassignWarden(hostelId));

  const refetchAll = () => {
    refetchPending();
    refetchApproved();
  };

  const handleApprove = async (w) => {
    if (!window.confirm(`Approve ${w.name} (${w.username}) as a warden? They'll immediately be able to sign in.`)) return;
    try {
      await approveMutation.mutate(w.userId);
      addToast(`${w.name} approved — they can now sign in`);
      refetchAll();
    } catch (err) {
      addToast(err.message || 'Failed to approve warden', 'error');
    }
  };

  const handleReject = async (w) => {
    if (!window.confirm(`Reject and remove ${w.name}'s (${w.username}) warden registration?`)) return;
    try {
      await rejectMutation.mutate(w.userId);
      addToast('Registration rejected');
      refetchAll();
    } catch (err) {
      addToast(err.message || 'Failed to reject warden', 'error');
    }
  };

  const handleAssign = async (staffId, hostelId) => {
    if (!hostelId) return;
    try {
      await assignMutation.mutate({ hostelId: Number(hostelId), staffId });
      addToast('Warden assigned to hostel');
      setAssigningStaffId(null);
      refetchAll();
    } catch (err) {
      addToast(err.message || 'Failed to assign warden', 'error');
    }
  };

  const handleUnassign = async (hostelId) => {
    if (!window.confirm('Remove this warden from the hostel?')) return;
    try {
      await unassignMutation.mutate(hostelId);
      addToast('Warden unassigned');
      refetchAll();
    } catch (err) {
      addToast(err.message || 'Failed to unassign warden', 'error');
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-800 tracking-tight">{t('nav_wardens')}</h2>
        <p className="text-sm text-slate-500 mt-1">
          Approve new warden registrations, then assign each approved warden to a hostel.
        </p>
      </div>

      <ErrorBanner message={pendingError} />

      {/* Pending approvals */}
      <Card className="p-5">
        <div className="flex items-center gap-2 font-semibold text-slate-800 mb-4">
          <Clock className="w-4 h-4 text-amber-500" /> Pending Approval
          {pending?.length > 0 && (
            <span className="ml-1 inline-flex items-center justify-center w-5 h-5 text-[11px] font-bold text-white bg-amber-500 rounded-full">
              {pending.length}
            </span>
          )}
        </div>
        {pendingLoading && <p className="text-sm text-slate-400">Loading…</p>}
        {!pendingLoading && (!pending || pending.length === 0) && (
          <p className="text-sm text-slate-400">No pending warden registrations right now.</p>
        )}
        <div className="space-y-2">
          {pending?.map((w) => (
            <div
              key={w.userId}
              className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 px-4 py-3 rounded-xl bg-amber-50/60 border border-amber-100"
            >
              <div>
                <p className="font-semibold text-slate-800 text-sm">{w.name}</p>
                <p className="text-xs text-slate-500">
                  {w.username} · {w.email}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <Button
                  variant="secondary"
                  className="px-3 py-1.5 h-auto text-xs border-emerald-200 text-emerald-700 hover:bg-emerald-50"
                  icon={ShieldCheck}
                  isLoading={approveMutation.isPending}
                  onClick={() => handleApprove(w)}
                >
                  Permit Access
                </Button>
                <Button
                  variant="secondary"
                  className="px-3 py-1.5 h-auto text-xs border-rose-200 text-rose-700 hover:bg-rose-50"
                  icon={ShieldX}
                  isLoading={rejectMutation.isPending}
                  onClick={() => handleReject(w)}
                >
                  Reject
                </Button>
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* Approved wardens + hostel assignment */}
      <Card className="p-5">
        <div className="flex items-center gap-2 font-semibold text-slate-800 mb-4">
          <UserCog className="w-4 h-4 text-indigo-500" /> Approved Wardens
        </div>
        <ErrorBanner message={approvedError} />
        {approvedLoading && <p className="text-sm text-slate-400">Loading…</p>}
        {!approvedLoading && (!approved || approved.length === 0) && (
          <p className="text-sm text-slate-400">No approved wardens yet — approve a pending request above.</p>
        )}
        <div className="space-y-2">
          {approved?.map((w) => (
            <div
              key={w.staffId}
              className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 px-4 py-3 rounded-xl bg-slate-50 border border-slate-100"
            >
              <div>
                <p className="font-semibold text-slate-800 text-sm">{w.name}</p>
                <p className="text-xs text-slate-500">
                  {w.username} · {w.email}
                </p>
              </div>

              <div className="flex items-center gap-2">
                {w.assignedHostelId ? (
                  <>
                    <Badge status="active">
                      <Building2 className="w-3 h-3 inline mr-1 -mt-0.5" />
                      {w.assignedHostelName}
                    </Badge>
                    <Button
                      variant="ghost"
                      className="px-2.5 py-1.5 h-auto text-xs text-rose-600 hover:bg-rose-50"
                      isLoading={unassignMutation.isPending}
                      onClick={() => handleUnassign(w.assignedHostelId)}
                    >
                      Unassign
                    </Button>
                  </>
                ) : assigningStaffId === w.staffId ? (
                  <Select
                    className="w-56"
                    defaultValue=""
                    onChange={(e) => handleAssign(w.staffId, e.target.value)}
                  >
                    <option value="" disabled>
                      Select a hostel…
                    </option>
                    {hostels?.map((h) => (
                      <option key={h.id} value={h.id}>
                        {h.name}
                      </option>
                    ))}
                  </Select>
                ) : (
                  <Button
                    variant="secondary"
                    className="px-3 py-1.5 h-auto text-xs"
                    icon={Building2}
                    onClick={() => setAssigningStaffId(w.staffId)}
                  >
                    Assign to Hostel
                  </Button>
                )}
              </div>
            </div>
          ))}
        </div>
        {(!hostels || hostels.length === 0) && (
          <p className="text-xs text-amber-600 mt-3">
            No hostels exist yet — create one on the Hostels page before assigning wardens.
          </p>
        )}
      </Card>

      {!pendingLoading && !approvedLoading && (!pending || pending.length === 0) && (!approved || approved.length === 0) && (
        <EmptyState
          icon={UserCog}
          title="No wardens yet"
          description="Once someone registers with the Warden role, their request will show up here for approval."
        />
      )}
    </div>
  );
};
