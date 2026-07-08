import { ScatterplotLayer } from '@deck.gl/layers';
import { useMemo } from 'react';

import type { TargetCurrent } from '../../targets/types/target.types';
import { colorForClassification } from '../utils/targetLayerStyle';

interface TargetLayerProps {
  targets: TargetCurrent[];
  onTargetClick: (targetId: string) => void;
}

export function useTargetLayer({ targets, onTargetClick }: TargetLayerProps) {
  return useMemo(() => {
    const positions = new Float32Array(targets.length * 3);
    const colors = new Uint8ClampedArray(targets.length * 4);

    targets.forEach((target, index) => {
      const positionIndex = index * 3;
      positions[positionIndex] = target.longitude;
      positions[positionIndex + 1] = target.latitude;
      positions[positionIndex + 2] = target.altitude;

      const color = colorForClassification(target.classification);
      const colorIndex = index * 4;
      colors[colorIndex] = color[0];
      colors[colorIndex + 1] = color[1];
      colors[colorIndex + 2] = color[2];
      colors[colorIndex + 3] = color[3];
    });

    return new ScatterplotLayer({
      id: 'target-layer',
      data: {
        length: targets.length,
        attributes: {
          getPosition: { value: positions, size: 3 },
          getFillColor: { value: colors, size: 4 },
        },
      },
      getRadius: 5,
      radiusUnits: 'pixels',
      pickable: true,
      stroked: true,
      getLineColor: [15, 23, 42, 180],
      lineWidthMinPixels: 1,
      onClick: (info) => {
        if (typeof info.index === 'number' && targets[info.index]) {
          onTargetClick(targets[info.index].targetId);
        }
      },
      updateTriggers: {
        getPosition: targets,
        getFillColor: targets,
      },
    });
  }, [onTargetClick, targets]);
}
