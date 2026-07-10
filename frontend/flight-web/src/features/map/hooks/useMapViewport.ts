import { useState } from 'react';

export interface MapViewport {
  latitude: number;
  longitude: number;
  zoom: number;
  pitch: number;
  bearing: number;
}

export function useMapViewport() {
  return useState<MapViewport>({
    latitude: 39.8283,
    longitude: -98.5795,
    zoom: 4,
    pitch: 0,
    bearing: 0,
  });
}
