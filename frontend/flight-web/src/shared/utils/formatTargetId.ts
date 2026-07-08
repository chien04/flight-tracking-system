export function formatTargetId(input: string) {
  return input.trim().padStart(4, '0').slice(-4);
}
