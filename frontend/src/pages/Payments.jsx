import React, { useState } from 'react';
import { Plus, CreditCard } from 'lucide-react';
import { Button, Input, Select, Modal, Badge, ErrorBanner, EmptyState } from '../components/ui';
import { DataTable } from '../components/table/DataTable';
import { useQuery, useMutation } from '../hooks/useApi';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';
import { useMyStudent } from '../hooks/useMyStudent';
import { paymentsService, studentsService } from '../lib/services';
import { useLanguage } from '../context/LanguageContext';

const STAFF_WITH_LIST_ACCESS = ['ADMIN', 'WARDEN'];

export const PaymentsView = () => {
  const { t } = useLanguage();
  const { user } = useAuth();
  const canViewAll = STAFF_WITH_LIST_ACCESS.includes(user?.role);
  const { student: myStudent, isLoading: studentLoading } = useMyStudent();
  const { data: students } = useQuery(() => studentsService.getAll(), [], { enabled: canViewAll });

  const {
    data: payments,
    isLoading,
    error,
    refetch,
  } = useQuery(
    () => (canViewAll ? paymentsService.getAll() : myStudent ? paymentsService.byStudent(myStudent.id) : Promise.resolve([])),
    [canViewAll, myStudent?.id],
    { enabled: canViewAll || !!myStudent }
  );

  const { addToast } = useToast();
  const [createOpen, setCreateOpen] = useState(false);
  const [formError, setFormError] = useState('');
  const [payingPayment, setPayingPayment] = useState(null); // payment pending a transaction ID (ONLINE method)
  const [payError, setPayError] = useState('');

  const createMutation = useMutation((payload) => paymentsService.create(payload));
  const statusMutation = useMutation(({ id, status, transactionId }) => paymentsService.updateStatus(id, status, transactionId));

  const handleCreate = async (e) => {
    e.preventDefault();
    setFormError('');
    const form = new FormData(e.target);
    const payload = {
      studentId: canViewAll ? Number(form.get('studentId')) : myStudent.id,
      amount: Number(form.get('amount')),
      paymentType: form.get('paymentType'),
      paymentMethod: form.get('paymentMethod'),
    };
    try {
      await createMutation.mutate(payload);
      addToast('Payment initiated!');
      setCreateOpen(false);
      refetch();
    } catch (err) {
      setFormError(err.message);
      addToast('Failed to create payment', 'error');
    }
  };

  const handleMarkPaid = async (payment) => {
    // Online payments need a transaction ID captured from the student before
    // they can be reconciled — collect it via a small prompt instead of
    // marking paid blind.
    if (payment.paymentMethod === 'ONLINE') {
      setPayError('');
      setPayingPayment(payment);
      return;
    }
    try {
      await statusMutation.mutate({ id: payment.id, status: 'SUCCESS' });
      addToast('Payment marked as paid');
      refetch();
    } catch (err) {
      addToast(err.message || 'Failed to update payment', 'error');
    }
  };

  const handleConfirmOnlinePayment = async (e) => {
    e.preventDefault();
    setPayError('');
    const form = new FormData(e.target);
    const transactionId = form.get('transactionId')?.trim();
    if (!transactionId) {
      setPayError('Transaction ID is required for online payments.');
      return;
    }
    try {
      await statusMutation.mutate({ id: payingPayment.id, status: 'SUCCESS', transactionId });
      addToast('Payment marked as paid');
      setPayingPayment(null);
      refetch();
    } catch (err) {
      setPayError(err.message || 'Failed to update payment');
    }
  };

  const columns = [
    ...(canViewAll ? [{ header: 'Student', accessor: 'studentName', cell: (r) => <span className="font-semibold text-slate-800">{r.studentName}</span> }] : []),
    { header: 'Type', accessor: 'paymentType', cell: (r) => <span className="text-slate-600">{r.paymentType || '—'}</span> },
    { header: 'Amount', accessor: 'amount', cell: (r) => <span className="font-semibold text-slate-900">₹{Number(r.amount).toLocaleString()}</span> },
    { header: 'Date', accessor: 'paymentDate', cell: (r) => <span className="text-slate-500">{r.paymentDate || '—'}</span> },
    { header: 'Status', accessor: 'status', cell: (r) => <Badge status={r.status}>{r.status}</Badge> },
    { header: 'Transaction ID', accessor: 'transactionId', cell: (r) => <span className="text-xs text-slate-400">{r.transactionId || '—'}</span> },
    ...(canViewAll
      ? [
          {
            header: 'Actions',
            cell: (r) =>
              r.status !== 'SUCCESS' ? (
                <Button variant="secondary" className="px-3 py-1.5 h-auto text-xs" onClick={() => handleMarkPaid(r)}>
                  Mark Paid
                </Button>
              ) : null,
          },
        ]
      : []),
  ];

  if (!canViewAll && studentLoading) {
    return <div className="text-sm text-slate-500">Loading your student profile…</div>;
  }

  if (!canViewAll && !myStudent) {
    return (
      <EmptyState
        title="No linked student profile"
        description="We couldn't find a Student record matching your account email/username. Ask an admin to link your account to view or make payments."
        icon={CreditCard}
      />
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-800 tracking-tight">{t('nav_payments')}</h2>
          <p className="text-sm text-slate-500 mt-1">{canViewAll ? 'Track and reconcile hostel fee payments.' : 'View and make your fee payments.'}</p>
        </div>
        <Button
          icon={Plus}
          onClick={() => {
            setFormError('');
            setCreateOpen(true);
          }}
        >
          New Payment
        </Button>
      </div>

      <ErrorBanner message={error} />

      <DataTable columns={columns} data={payments} isLoading={isLoading} emptyMessage="No payments found." />

      {createOpen && (
        <Modal title="New Payment" onClose={() => setCreateOpen(false)}>
          <form onSubmit={handleCreate} className="p-6 space-y-4">
            <ErrorBanner message={formError} />
            {canViewAll && (
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
            )}
            <Input label="Amount" name="amount" type="number" step="0.01" min="0" required placeholder="e.g. 15000" />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Select label="Payment Type" name="paymentType" defaultValue="HOSTEL_FEE">
                <option value="HOSTEL_FEE">Hostel Fee</option>
                <option value="MESS_FEE">Mess Fee</option>
                <option value="SECURITY_DEPOSIT">Security Deposit</option>
                <option value="FINE">Fine</option>
                <option value="OTHER">Other</option>
              </Select>
              <Select label="Payment Method" name="paymentMethod" defaultValue="ONLINE">
                <option value="ONLINE">Online</option>
                <option value="CASH">Cash</option>
                <option value="CHEQUE">Cheque</option>
                <option value="CARD">Card</option>
              </Select>
            </div>
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setCreateOpen(false)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={createMutation.isPending}>
                Submit Payment
              </Button>
            </div>
          </form>
        </Modal>
      )}
      {payingPayment && (
        <Modal title={`Confirm Online Payment — ${payingPayment.studentName || ''}`} onClose={() => setPayingPayment(null)}>
          <form onSubmit={handleConfirmOnlinePayment} className="p-6 space-y-4">
            <ErrorBanner message={payError} />
            <p className="text-sm text-slate-500">
              This payment was made online. Enter the transaction ID from the student to confirm and mark it paid.
            </p>
            <Input
              label="Transaction ID"
              name="transactionId"
              required
              autoFocus
              placeholder="e.g. TXN123456789"
            />
            <div className="pt-4 flex justify-end gap-3 border-t border-slate-100 mt-6">
              <Button variant="ghost" onClick={() => setPayingPayment(null)}>
                {t('cancel')}
              </Button>
              <Button type="submit" isLoading={statusMutation.isPending}>
                Mark Paid
              </Button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
