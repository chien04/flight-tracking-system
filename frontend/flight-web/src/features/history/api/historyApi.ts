import { axiosClient } from '../../../shared/lib/axiosClient';
import type { TargetHistoryPoint, TargetHistoryQuery } from '../types/history.types';

export async function getTargetHistory(
  targetId: string,
  query: TargetHistoryQuery,
  signal?: AbortSignal,
) {
  const response = await axiosClient.get<TargetHistoryPoint[]>(`/api/targets/${targetId}/history`, {
    params: query,
    signal,
  });

  return response.data;
}
