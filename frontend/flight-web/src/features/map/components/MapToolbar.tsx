import { RadioTower } from 'lucide-react';

import { ErrorMessage } from '../../../shared/components/ErrorMessage';
import { Loading } from '../../../shared/components/Loading';
import { formatTime } from '../../../shared/utils/formatTime';
import { TargetFilterPanel } from '../../targets/components/TargetFilterPanel';
import { TargetSearchBox } from '../../targets/components/TargetSearchBox';
import type { TargetClassification } from '../../targets/types/target.types';

interface MapToolbarProps {
  targetCount: number;
  filter: TargetClassification | 'ALL';
  lastBatchAt: number | null;
  lastRenderLatencyMs: number | null;
  socketConnected: boolean;
  loading: boolean;
  error: string | null;
  onFilterChange: (value: TargetClassification | 'ALL') => void;
  onSearch: (targetId: string) => void;
}

export function MapToolbar({
  targetCount,
  filter,
  lastBatchAt,
  lastRenderLatencyMs,
  socketConnected,
  loading,
  error,
  onFilterChange,
  onSearch,
}: MapToolbarProps) {
  return (
    <header className="map-toolbar">
      <div className="brand-block">
        <RadioTower aria-hidden="true" size={22} />
        <div>
          <p className="eyebrow">Flight Tracking</p>
          <h1>Target operations</h1>
        </div>
      </div>

      <div className="toolbar-controls">
        <TargetSearchBox onSelect={onSearch} />
        <TargetFilterPanel onChange={onFilterChange} value={filter} />
      </div>

      <div className="telemetry-strip">
        <span>{targetCount.toLocaleString()} targets</span>
        <span className={socketConnected ? 'status-good' : 'status-muted'}>
          {socketConnected ? 'Live' : 'Offline'}
        </span>
        {lastBatchAt ? <span>{formatTime(lastBatchAt)}</span> : null}
        {lastRenderLatencyMs !== null ? <span>Render {lastRenderLatencyMs} ms</span> : null}
        {loading ? <Loading /> : null}
        {error ? <ErrorMessage message={error} /> : null}
      </div>
    </header>
  );
}
