import { api } from './apiClient';

// --- /admin/users (AdminController) ---
export const adminService = {
  getAllUsers: () => api.get('/admin/users'),
  enableUser: (id) => api.put(`/admin/users/${id}/enable`),
  disableUser: (id) => api.put(`/admin/users/${id}/disable`),
};

// --- /dashboard (DashboardController) ---
export const dashboardService = {
  getStats: () => api.get('/dashboard'),
};

// --- /hostels (HostelController) ---
export const hostelsService = {
  getAll: () => api.get('/hostels'),
  getById: (id) => api.get(`/hostels/${id}`),
  create: (payload) => api.post('/hostels', payload),
  update: (id, payload) => api.put(`/hostels/${id}`, payload),
  delete: (id) => api.delete(`/hostels/${id}`),
  assignWarden: (hostelId, staffId) => api.put(`/hostels/${hostelId}/assign-warden/${staffId}`),
  unassignWarden: (hostelId) => api.delete(`/hostels/${hostelId}/assign-warden`),
};

// --- /warden (WardenController) ---
// Warden approval workflow: a warden self-registers (disabled + notifies
// admins), an admin approves/rejects here, then assigns them to a hostel
// via hostelsService.assignWarden.
export const wardenService = {
  getApproved: () => api.get('/warden/list'),
  getPending: () => api.get('/warden/pending'),
  approve: (userId) => api.put(`/warden/${userId}/approve`),
  reject: (userId) => api.delete(`/warden/${userId}/reject`),
};

// --- /blocks (BlockController) ---
export const blocksService = {
  getAll: (hostelId) => api.get('/blocks', hostelId ? { params: { hostelId } } : undefined),
  getById: (id) => api.get(`/blocks/${id}`),
  create: (payload) => api.post('/blocks', payload),
  update: (id, payload) => api.put(`/blocks/${id}`, payload),
  delete: (id) => api.delete(`/blocks/${id}`),
};

// --- /floors (FloorController) ---
export const floorsService = {
  getAll: (blockId) => api.get('/floors', blockId ? { params: { blockId } } : undefined),
  getById: (id) => api.get(`/floors/${id}`),
  create: (payload) => api.post('/floors', payload),
  update: (id, payload) => api.put(`/floors/${id}`, payload),
  delete: (id) => api.delete(`/floors/${id}`),
};

// --- /students (StudentController) ---
export const studentsService = {
  getAll: () => api.get('/students'),
  getById: (id) => api.get(`/students/${id}`),
  getMe: () => api.get('/students/me'),
  create: (payload) => api.post('/students', payload),
  update: (id, payload) => api.put(`/students/${id}`, payload),
  delete: (id) => api.delete(`/students/${id}`),
};

// --- /rooms (RoomController) ---
export const roomsService = {
  getAll: () => api.get('/rooms'),
  getAvailable: () => api.get('/rooms/available'),
  getById: (id) => api.get(`/rooms/${id}`),
  create: (payload) => api.post('/rooms', payload),
  update: (id, payload) => api.put(`/rooms/${id}`, payload),
  delete: (id) => api.delete(`/rooms/${id}`),
  allocate: (studentId, roomId) => api.post('/rooms/allocate', { studentId, roomId }),
  vacate: (studentId) => api.post(`/rooms/vacate/${studentId}`),
};

// --- /attendance (AttendanceController) ---
export const attendanceService = {
  mark: (payload) => api.post('/attendance', payload),
  byStudent: (studentId) => api.get(`/attendance/student/${studentId}`),
  byDate: (date) => api.get('/attendance/date', { params: { date } }),
};

// --- /complaints (ComplaintController) ---
export const complaintsService = {
  getAll: () => api.get('/complaints'),
  getById: (id) => api.get(`/complaints/${id}`),
  byStudent: (studentId) => api.get(`/complaints/student/${studentId}`),
  create: (studentId, payload) => api.post(`/complaints/student/${studentId}`, payload),
  updateStatus: (id, status) => api.put(`/complaints/${id}/status`, null, { params: { status } }),
  reply: (id, message) => api.post(`/complaints/${id}/reply`, { message }),
};

// --- /notifications (NotificationController) ---
export const notificationsService = {
  byUser: (userId) => api.get(`/notifications/user/${userId}`),
  unreadByUser: (userId) => api.get(`/notifications/user/${userId}/unread`),
  markAsRead: (id) => api.put(`/notifications/${id}/read`),
};

// --- /payments (PaymentController) ---
export const paymentsService = {
  getAll: () => api.get('/payments'),
  getById: (id) => api.get(`/payments/${id}`),
  byStudent: (studentId) => api.get(`/payments/student/${studentId}`),
  create: (payload) => api.post('/payments', payload),
  updateStatus: (id, status, transactionId) =>
    api.put(`/payments/${id}/status`, null, { params: { status, transactionId } }),
};
