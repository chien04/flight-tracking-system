export function formatTime(timestamp: number) {
  const date = new Date(timestamp);
  const time = new Intl.DateTimeFormat('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  }).format(date);

  return `${time}.${String(date.getMilliseconds()).padStart(3, '0')}`;
}
