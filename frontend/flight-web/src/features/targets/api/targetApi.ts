import { axiosClient } from '../../../shared/lib/axiosClient';
import type { TargetCurrent } from '../types/target.types';

export async function getTargets() {
  const response = await axiosClient.get<TargetCurrent[]>('/api/targets');
  return response.data;
}

export async function getTarget(targetId: string) {
  const response = await axiosClient.get<TargetCurrent>(`/api/targets/${targetId}`);
  return response.data;
}
