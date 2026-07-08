import type { Client, IMessage } from '@stomp/stompjs';

import { createWebSocketClient } from '../../../shared/lib/websocketClient';
import type { TargetCurrent, TargetRealtimeBatchMessage } from '../types/target.types';

const REALTIME_TOPIC = '/topic/targets/realtime';

export interface TargetSocketHandlers {
  onBatch: (targets: TargetCurrent[], batchTimestamp: number) => void;
  onConnectionChange: (connected: boolean) => void;
}

export function connectTargetSocket(handlers: TargetSocketHandlers) {
  const client: Client = createWebSocketClient();

  client.onConnect = () => {
    handlers.onConnectionChange(true);
    client.subscribe(REALTIME_TOPIC, (message) => {
      const batch = parseBatch(message);
      handlers.onBatch(toCurrentTargets(batch), batch.timestamp);
    });
  };

  client.onDisconnect = () => handlers.onConnectionChange(false);
  client.onWebSocketClose = () => handlers.onConnectionChange(false);
  client.activate();

  return () => {
    handlers.onConnectionChange(false);
    void client.deactivate();
  };
}

function parseBatch(message: IMessage): TargetRealtimeBatchMessage {
  return JSON.parse(message.body) as TargetRealtimeBatchMessage;
}

function toCurrentTargets(batch: TargetRealtimeBatchMessage): TargetCurrent[] {
  if (batch.type === 'TARGET_UPDATE_BATCH_COMPACT') {
    return batch.targetIds.map((targetId, index) => ({
      targetId,
      latitude: batch.latitudes[index],
      longitude: batch.longitudes[index],
      altitude: batch.altitudes[index],
      classification: batch.classifications[index],
      updatedAt: batch.timestamp,
    }));
  }

  return batch.targets.map((target) => ({
    targetId: target.targetId,
    latitude: target.latitude,
    longitude: target.longitude,
    altitude: target.altitude,
    classification: target.classification,
    updatedAt: target.timestamp,
  }));
}
