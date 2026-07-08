export type TargetClassification = 'ENEMY' | 'FRIEND' | 'UNKNOWN';

export interface TargetCurrent {
  targetId: string;
  latitude: number;
  longitude: number;
  altitude: number;
  classification: TargetClassification;
  updatedAt: number;
}

export interface TargetUpdateEvent {
  targetId: string;
  latitude: number;
  longitude: number;
  altitude: number;
  classification: TargetClassification;
  timestamp: number;
}

export interface TargetRealtimeObjectBatchMessage {
  type: 'TARGET_UPDATE_BATCH';
  timestamp: number;
  targets: TargetUpdateEvent[];
}

export interface TargetRealtimeCompactBatchMessage {
  type: 'TARGET_UPDATE_BATCH_COMPACT';
  timestamp: number;
  targetIds: string[];
  latitudes: number[];
  longitudes: number[];
  altitudes: number[];
  classifications: TargetClassification[];
}

export type TargetRealtimeBatchMessage =
  | TargetRealtimeObjectBatchMessage
  | TargetRealtimeCompactBatchMessage;
