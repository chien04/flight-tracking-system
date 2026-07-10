import axios from 'axios';

import { axiosClient } from '../../../shared/lib/axiosClient';
import type { TargetHistoryPoint, TargetHistoryQuery } from '../types/history.types';

export async function getTargetHistory(
  targetId: string,
  query: TargetHistoryQuery,
  signal?: AbortSignal,
) {
  try {
    const response = await axiosClient.get<TargetHistoryPoint[]>(`/api/targets/${targetId}/history`, {
      params: query,
      signal,
    });

    return response.data;
  } catch (error) {
    if (axios.isAxiosError<{ message?: string }>(error)) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      if (error.message === 'Network Error') {
        throw new Error('Cannot reach backend history API');
      }
    }

    throw error;
  }
}
