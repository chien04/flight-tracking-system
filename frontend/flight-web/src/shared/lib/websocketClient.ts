import { Client } from '@stomp/stompjs';

import { env } from '../config/env';

export function createWebSocketClient() {
  return new Client({
    brokerURL: env.websocketUrl,
    reconnectDelay: 1_000,
    heartbeatIncoming: 10_000,
    heartbeatOutgoing: 10_000,
  });
}
