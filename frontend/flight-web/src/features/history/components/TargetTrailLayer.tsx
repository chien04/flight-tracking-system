import { PathLayer } from '@deck.gl/layers';
import { useMemo } from 'react';

import type { TargetHistoryPoint } from '../types/history.types';

interface TargetTrailLayerProps {
  points: TargetHistoryPoint[];
}

type TrailPosition = [number, number, number];

interface TrailPath {
  path: TrailPosition[];
}

export function useTargetTrailLayer({ points }: TargetTrailLayerProps) {
  return useMemo(() => {
    if (points.length < 2) {
      return null;
    }

    const path = points.map<TrailPosition>((point) => [
      point.longitude,
      point.latitude,
      point.altitude,
    ]);

    return new PathLayer<TrailPath>({
      id: 'target-history-trail-layer',
      data: [{ path }],
      getPath: (trail) => trail.path,
      getColor: [223, 91, 46, 220],
      getWidth: 3,
      widthMinPixels: 2,
      widthUnits: 'pixels',
      rounded: true,
      pickable: false,
    });
  }, [points]);
}
