import type { TargetClassification } from '../../targets/types/target.types';

export function colorForClassification(classification: TargetClassification): [number, number, number, number] {
  switch (classification) {
    case 'ENEMY':
      return [221, 72, 72, 220];
    case 'FRIEND':
      return [44, 150, 108, 220];
    case 'UNKNOWN':
      return [230, 174, 70, 220];
  }
}
