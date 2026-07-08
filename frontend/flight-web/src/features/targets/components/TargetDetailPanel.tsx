import { Route, X } from 'lucide-react';

import { formatTime } from '../../../shared/utils/formatTime';
import type { TargetCurrent } from '../types/target.types';

interface TargetDetailPanelProps {
  target: TargetCurrent | null;
  historyVisible: boolean;
  historyLoading: boolean;
  onClose: () => void;
  onToggleHistory: () => void;
}

export function TargetDetailPanel({
  target,
  historyVisible,
  historyLoading,
  onClose,
  onToggleHistory,
}: TargetDetailPanelProps) {
  if (!target) {
    return null;
  }

  return (
    <aside className="target-detail" aria-label="Selected target">
      <div className="panel-heading">
        <div>
          <p className="eyebrow">Target</p>
          <h2>{target.targetId}</h2>
        </div>
        <button aria-label="Close target detail" className="icon-button" onClick={onClose} type="button">
          <X size={18} />
        </button>
      </div>

      <dl className="detail-grid">
        <div>
          <dt>Class</dt>
          <dd>{target.classification}</dd>
        </div>
        <div>
          <dt>Altitude</dt>
          <dd>{Math.round(target.altitude).toLocaleString()} m</dd>
        </div>
        <div>
          <dt>Latitude</dt>
          <dd>{target.latitude.toFixed(5)}</dd>
        </div>
        <div>
          <dt>Longitude</dt>
          <dd>{target.longitude.toFixed(5)}</dd>
        </div>
        <div>
          <dt>Updated</dt>
          <dd>{formatTime(target.updatedAt)}</dd>
        </div>
      </dl>

      <div className="detail-actions">
        <button
          aria-pressed={historyVisible}
          className="action-button"
          onClick={onToggleHistory}
          title="Toggle trail"
          type="button"
        >
          <Route size={16} />
          {historyLoading ? 'Loading' : 'Trail'}
        </button>
      </div>
    </aside>
  );
}
