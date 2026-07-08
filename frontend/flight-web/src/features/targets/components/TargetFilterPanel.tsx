import type { TargetClassification } from '../types/target.types';

const FILTERS: Array<TargetClassification | 'ALL'> = ['ALL', 'FRIEND', 'ENEMY', 'UNKNOWN'];

interface TargetFilterPanelProps {
  value: TargetClassification | 'ALL';
  onChange: (value: TargetClassification | 'ALL') => void;
}

export function TargetFilterPanel({ value, onChange }: TargetFilterPanelProps) {
  return (
    <div className="segmented-control" aria-label="Target classification filter">
      {FILTERS.map((filter) => (
        <button
          aria-pressed={value === filter}
          className="segment-button"
          key={filter}
          onClick={() => onChange(filter)}
          type="button"
        >
          {filter}
        </button>
      ))}
    </div>
  );
}
