import { formatTime } from '../../../shared/utils/formatTime';
import type { TargetHistoryPoint } from '../types/history.types';

interface HistoryRangeOption {
  label: string;
  value: number;
}

interface TargetHistoryPanelProps {
  targetId: string | null;
  points: TargetHistoryPoint[];
  loading: boolean;
  error: string | null;
  rangeMs: number;
  rangeOptions: HistoryRangeOption[];
  onRangeChange: (rangeMs: number) => void;
}

export function TargetHistoryPanel({
  targetId,
  points,
  loading,
  error,
  rangeMs,
  rangeOptions,
  onRangeChange,
}: TargetHistoryPanelProps) {
  if (!targetId) {
    return null;
  }

  const firstPoint = points[0] ?? null;
  const lastPoint = points.length > 0 ? points[points.length - 1] : null;

  return (
    <aside className="target-history-panel" aria-label="Target history">
      <div className="panel-heading">
        <div>
          <p className="eyebrow">History</p>
          <h2>{targetId}</h2>
        </div>
        <span className="history-count">{loading ? 'Loading' : `${points.length.toLocaleString()} points`}</span>
      </div>

      <div className="history-range" aria-label="History range">
        {rangeOptions.map((option) => (
          <button
            aria-pressed={option.value === rangeMs}
            className="segment-button"
            key={option.value}
            onClick={() => onRangeChange(option.value)}
            type="button"
          >
            {option.label}
          </button>
        ))}
      </div>

      <dl className="history-meta">
        <div>
          <dt>Start</dt>
          <dd>{firstPoint ? formatTime(firstPoint.timestamp) : '-'}</dd>
        </div>
        <div>
          <dt>End</dt>
          <dd>{lastPoint ? formatTime(lastPoint.timestamp) : '-'}</dd>
        </div>
      </dl>

      {error ? <p className="history-error">{error}</p> : null}
    </aside>
  );
}
