export const env = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  websocketUrl: import.meta.env.VITE_WEBSOCKET_URL ?? 'ws://localhost:8080/ws',
} as const;
