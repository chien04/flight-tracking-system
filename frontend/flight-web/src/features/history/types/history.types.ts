import type { TargetClassification } from '../../targets/types/target.types';

export interface TargetHistoryPoint {
  targetId: string;
  latitude: number;
  longitude: number;
  altitude: number;
  classification: TargetClassification;
  timestamp: number;
}

export interface TargetHistoryQuery {
  from: number;
  to: number;
  sampleMs: number;
}
