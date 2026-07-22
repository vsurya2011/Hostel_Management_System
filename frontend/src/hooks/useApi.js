import { useCallback, useEffect, useState } from 'react';

// A small, dependency-free stand-in for react-query. Good enough for this
// app's needs: fetch on mount / when deps change, expose refetch, track
// loading + error state consistently across pages.
export function useQuery(queryFn, deps = [], { enabled = true } = {}) {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(enabled);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await queryFn();
      setData(result);
    } catch (err) {
      setError(err.message || 'An error occurred');
    } finally {
      setIsLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  useEffect(() => {
    if (enabled) fetchData();
  }, [enabled, fetchData]);

  return { data, isLoading, error, refetch: fetchData };
}

export function useMutation(mutationFn) {
  const [isPending, setIsPending] = useState(false);
  const [error, setError] = useState(null);

  const mutate = async (variables) => {
    setIsPending(true);
    setError(null);
    try {
      const result = await mutationFn(variables);
      return result;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      setIsPending(false);
    }
  };

  return { mutate, isPending, error };
}
