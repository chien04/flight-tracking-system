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
    latitude: 21.0285,
    longitude: 105.8542,
    zoom: 10,
    pitch: 0,
    bearing: 0,
  });
}
