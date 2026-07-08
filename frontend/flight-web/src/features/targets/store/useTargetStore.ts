import { create } from 'zustand';

import type { TargetClassification, TargetCurrent } from '../types/target.types';

interface TargetState {
  targetsById: Map<string, TargetCurrent>;
  selectedTargetId: string | null;
  filterClassification: TargetClassification | 'ALL';
  socketConnected: boolean;
  lastBatchAt: number | null;
  setInitialTargets: (targets: TargetCurrent[]) => void;
  updateTargetsBatch: (targets: TargetCurrent[], batchTimestamp: number) => void;
  setSelectedTargetId: (targetId: string | null) => void;
  setFilterClassification: (classification: TargetClassification | 'ALL') => void;
  setSocketConnected: (connected: boolean) => void;
}

export const useTargetStore = create<TargetState>((set) => ({
  targetsById: new Map(),
  selectedTargetId: null,
  filterClassification: 'ALL',
  socketConnected: false,
  lastBatchAt: null,
  setInitialTargets: (targets) => {
    set({
      targetsById: new Map(targets.map((target) => [target.targetId, target])),
    });
  },
  updateTargetsBatch: (targets, batchTimestamp) => {
    set((state) => {
      const nextTargets = new Map(state.targetsById);
      for (const target of targets) {
        nextTargets.set(target.targetId, target);
      }

      return {
        targetsById: nextTargets,
        lastBatchAt: batchTimestamp,
      };
    });
  },
  setSelectedTargetId: (targetId) => set({ selectedTargetId: targetId }),
  setFilterClassification: (classification) => set({ filterClassification: classification }),
  setSocketConnected: (connected) => set({ socketConnected: connected }),
}));
