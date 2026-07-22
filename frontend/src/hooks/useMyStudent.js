import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { studentsService } from '../lib/services';

// Resolves the Student record for the currently logged-in user via the
// backend's GET /students/me (resolved server-side from the JWT, using the
// real user_id link — see StudentController#getMyProfile). A 404 here just
// means an admin/warden hasn't linked this account to a Student profile yet,
// which is an expected, non-error state (not "something went wrong").
export function useMyStudent() {
  const { user } = useAuth();
  const [student, setStudent] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    async function resolve() {
      if (!user || user.role !== 'STUDENT') {
        setIsLoading(false);
        return;
      }
      setIsLoading(true);
      setError(null);
      try {
        const result = await studentsService.getMe();
        if (!cancelled) setStudent(result);
      } catch (err) {
        if (!cancelled) {
          if (err.status === 404) {
            setStudent(null); // not linked yet — expected, not an error banner
          } else {
            setError(err.message);
          }
        }
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }
    resolve();
    return () => {
      cancelled = true;
    };
  }, [user]);

  return { student, isLoading, error };
}
