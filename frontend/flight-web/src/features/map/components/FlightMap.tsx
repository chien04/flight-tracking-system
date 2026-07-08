import DeckGL from '@deck.gl/react';
import type { StyleSpecification } from 'maplibre-gl';
import { useCallback, useEffect, useMemo, useState } from 'react';
import Map from 'react-map-gl/maplibre';

import 'maplibre-gl/dist/maplibre-gl.css';

import { getTargetHistory } from '../../history/api/historyApi';
import { TargetHistoryPanel } from '../../history/components/TargetHistoryPanel';
import { useTargetTrailLayer } from '../../history/components/TargetTrailLayer';
import type { TargetHistoryPoint } from '../../history/types/history.types';
import { getTargets } from '../../targets/api/targetApi';
import { TargetDetailPanel } from '../../targets/components/TargetDetailPanel';
import { connectTargetSocket } from '../../targets/socket/targetSocket';
import { useTargetStore } from '../../targets/store/useTargetStore';
import { useMapViewport } from '../hooks/useMapViewport';
import { useTargetLayer } from './TargetLayer';
import { MapToolbar } from './MapToolbar';

const HISTORY_RANGE_OPTIONS = [
  { label: '5m', value: 5 * 60 * 1000 },
  { label: '10m', value: 10 * 60 * 1000 },
  { label: '30m', value: 30 * 60 * 1000 },
] as const;

const DEFAULT_HISTORY_RANGE_MS = HISTORY_RANGE_OPTIONS[1].value;

function sampleMsForRange(rangeMs: number) {
  return Math.max(1000, Math.round(rangeMs / 600));
}

const MAP_STYLE: StyleSpecification = {
  version: 8,
  sources: {
    osm: {
      type: 'raster',
      tiles: [
        'https://a.tile.openstreetmap.org/{z}/{x}/{y}.png',
        'https://b.tile.openstreetmap.org/{z}/{x}/{y}.png',
        'https://c.tile.openstreetmap.org/{z}/{x}/{y}.png',
      ],
      tileSize: 256,
      attribution: 'OpenStreetMap',
    },
  },
  layers: [
    {
      id: 'osm',
      type: 'raster',
      source: 'osm',
    },
  ],
};

export function FlightMap() {
  const [viewState, setViewState] = useMapViewport();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [historyVisible, setHistoryVisible] = useState(false);
  const [historyRangeMs, setHistoryRangeMs] = useState(DEFAULT_HISTORY_RANGE_MS);
  const [historyPoints, setHistoryPoints] = useState<TargetHistoryPoint[]>([]);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyError, setHistoryError] = useState<string | null>(null);
  const {
    targetsById,
    selectedTargetId,
    filterClassification,
    socketConnected,
    lastBatchAt,
    setInitialTargets,
    updateTargetsBatch,
    setSelectedTargetId,
    setFilterClassification,
    setSocketConnected,
  } = useTargetStore();

  useEffect(() => {
    let cancelled = false;

    getTargets()
      .then((targets) => {
        if (!cancelled) {
          setInitialTargets(targets);
          setError(null);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setError('Snapshot unavailable');
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [setInitialTargets]);

  useEffect(() => {
    return connectTargetSocket({
      onBatch: updateTargetsBatch,
      onConnectionChange: setSocketConnected,
    });
  }, [setSocketConnected, updateTargetsBatch]);

  useEffect(() => {
    if (!selectedTargetId || !historyVisible) {
      setHistoryPoints([]);
      setHistoryLoading(false);
      setHistoryError(null);
      return;
    }

    const abortController = new AbortController();
    const to = Date.now();
    const from = to - historyRangeMs;

    setHistoryLoading(true);
    setHistoryError(null);

    getTargetHistory(
      selectedTargetId,
      {
        from,
        to,
        sampleMs: sampleMsForRange(historyRangeMs),
      },
      abortController.signal,
    )
      .then((points) => {
        setHistoryPoints(points);
      })
      .catch((requestError: unknown) => {
        if (abortController.signal.aborted) {
          return;
        }

        setHistoryPoints([]);
        setHistoryError(requestError instanceof Error ? requestError.message : 'History unavailable');
      })
      .finally(() => {
        if (!abortController.signal.aborted) {
          setHistoryLoading(false);
        }
      });

    return () => {
      abortController.abort();
    };
  }, [historyRangeMs, historyVisible, selectedTargetId]);

  const allTargets = useMemo(() => Array.from(targetsById.values()), [targetsById]);
  const visibleTargets = useMemo(() => {
    if (filterClassification === 'ALL') {
      return allTargets;
    }

    return allTargets.filter((target) => target.classification === filterClassification);
  }, [allTargets, filterClassification]);

  const selectedTarget = selectedTargetId ? targetsById.get(selectedTargetId) ?? null : null;

  const handleTargetClick = useCallback(
    (targetId: string) => {
      setSelectedTargetId(targetId);
      setHistoryVisible(true);
    },
    [setSelectedTargetId],
  );

  const targetLayer = useTargetLayer({
    targets: visibleTargets,
    onTargetClick: handleTargetClick,
  });
  const targetTrailLayer = useTargetTrailLayer({
    points: historyVisible ? historyPoints : [],
  });
  const layers = useMemo(
    () => (targetTrailLayer ? [targetTrailLayer, targetLayer] : [targetLayer]),
    [targetLayer, targetTrailLayer],
  );

  function handleSearch(targetId: string) {
    const target = targetsById.get(targetId);
    if (!target) {
      setError(`Target ${targetId} not found`);
      return;
    }

    setSelectedTargetId(targetId);
    setHistoryVisible(true);
    setViewState((current) => ({
      ...current,
      latitude: target.latitude,
      longitude: target.longitude,
      zoom: Math.max(current.zoom, 12),
    }));
    setError(null);
  }

  function handleCloseDetail() {
    setSelectedTargetId(null);
    setHistoryVisible(false);
    setHistoryPoints([]);
    setHistoryError(null);
  }

  function handleToggleHistory() {
    setHistoryVisible((current) => !current);
  }

  return (
    <main className="map-workspace">
      <MapToolbar
        error={error}
        filter={filterClassification}
        lastBatchAt={lastBatchAt}
        loading={loading}
        onFilterChange={setFilterClassification}
        onSearch={handleSearch}
        socketConnected={socketConnected}
        targetCount={visibleTargets.length}
      />

      <DeckGL
        controller
        layers={layers}
        onViewStateChange={({ viewState: nextViewState }) =>
          setViewState(nextViewState as typeof viewState)
        }
        viewState={viewState}
      >
        <Map mapStyle={MAP_STYLE} reuseMaps />
      </DeckGL>

      <TargetHistoryPanel
        error={historyError}
        loading={historyLoading}
        onRangeChange={setHistoryRangeMs}
        points={historyPoints}
        rangeMs={historyRangeMs}
        rangeOptions={[...HISTORY_RANGE_OPTIONS]}
        targetId={historyVisible ? selectedTargetId : null}
      />

      <TargetDetailPanel
        historyLoading={historyLoading}
        historyVisible={historyVisible}
        onClose={handleCloseDetail}
        onToggleHistory={handleToggleHistory}
        target={selectedTarget}
      />
    </main>
  );
}
