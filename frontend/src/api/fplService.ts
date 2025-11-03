import apiClient from './apiClient';

export interface GenerateSquadPayload {
  budget: number;
  formation: string;
  mustHavePlayers: number[];
  excludedPlayers: number[];
}

// Frontend Player interface
export interface Player {
  id: number;
  name: string;
  position: string;
  team: string;
  cost: number;
}

// API response types
export interface ApiPlayer {
  playerId: number;
  name: string;
  position: string;
  team: string;
  price: number;
  totalPoints: number;
  form: number;
  valueForMoney: number;
  nextFixtures: string[];
  injuryStatus: string;
  chanceOfPlaying: number | null;
  aiScore: number | null;
  recommendation: string;
}

export interface ApiSquadResult {
  selectedPlayers: ApiPlayer[];
  totalCost: number;
  projectedPoints: number;
  aiAnalysis: string;
  positionBreakdown: Record<string, number>;
}

// Frontend-friendly SquadResult
export interface SquadResult {
  players: Player[];
  analysis?: string;
}

export const generateSquad = async (
  payload: GenerateSquadPayload
): Promise<ApiSquadResult> => {
  const response = await apiClient.post<ApiSquadResult>(
    '/api/v1/fpl/squad/generate',
    payload
  );
  return response.data;
};
