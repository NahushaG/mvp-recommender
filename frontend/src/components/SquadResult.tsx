import React from 'react';
import PlayerCard, { type Player } from './PlayerCard';

export interface SquadResultProps {
  players: Player[];
  analysis?: string;
  formation?: string;
}

// Helper: group players by position
const groupByPosition = (players: Player[]) => {
  const positions: Record<string, Player[]> = {
    Goalkeeper: [],
    Defender: [],
    Midfielder: [],
    Forward: [],
  };

  players.forEach((p) => {
    if (positions[p.position]) {
      positions[p.position].push(p);
    }
  });

  return positions;
};

// Color mapping for positions
const positionColors: Record<string, string> = {
  Goalkeeper: '#ffcccb',
  Defender: '#add8e6',
  Midfielder: '#90ee90',
  Forward: '#ffd700',
};

const SquadResult: React.FC<SquadResultProps> = ({ players, analysis }) => {
  const grouped = groupByPosition(players);

  return (
    <div style={{ marginTop: '1rem' }}>
      <h3 style={{ textAlign: 'center', color: '#333' }}>Generated Squad</h3>

      {/* Football pitch layout */}
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: '2rem',
          marginBottom: '2rem',
          padding: '1rem',
          backgroundColor: '#1e7e34', // pitch green
          borderRadius: '12px',
          boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
        }}
      >
        {/* Goalkeeper */}
        <div style={{ display: 'flex', gap: '1rem' }}>
          {grouped.Goalkeeper.map((p) => (
            <PlayerCard
              key={p.id}
              player={p}
              style={{ backgroundColor: positionColors[p.position] }}
            />
          ))}
        </div>

        {/* Defenders */}
        <div style={{ display: 'flex', gap: '1rem' }}>
          {grouped.Defender.map((p) => (
            <PlayerCard
              key={p.id}
              player={p}
              style={{ backgroundColor: positionColors[p.position] }}
            />
          ))}
        </div>

        {/* Midfielders */}
        <div style={{ display: 'flex', gap: '1rem' }}>
          {grouped.Midfielder.map((p) => (
            <PlayerCard
              key={p.id}
              player={p}
              style={{ backgroundColor: positionColors[p.position] }}
            />
          ))}
        </div>

        {/* Forwards */}
        <div style={{ display: 'flex', gap: '1rem' }}>
          {grouped.Forward.map((p) => (
            <PlayerCard
              key={p.id}
              player={p}
              style={{ backgroundColor: positionColors[p.position] }}
            />
          ))}
        </div>
      </div>

      {/* Structured Analysis */}
      {analysis && (
        <div
          style={{
            marginTop: '1rem',
            maxWidth: '800px',
            marginLeft: 'auto',
            marginRight: 'auto',
            textAlign: 'left',
            padding: '1rem',
            border: '1px solid #ccc',
            borderRadius: '0.5rem',
            backgroundColor: '#f4f4f9',
            boxShadow: '0 2px 6px rgba(0,0,0,0.1)',
          }}
        >
          <h4 style={{ color: '#333', marginBottom: '0.5rem' }}>Analysis</h4>
          <div>
            <strong>1) Squad Balance Assessment:</strong>
            <p>This squad is well-balanced with decent point returns across all positions.</p>
          </div>

          <div>
            <strong>2) Weak Areas:</strong>
            <ul>
              <li>The midfield lacks high-scoring premium options.</li>
              <li>Only Semenyo provides consistent point returns in midfield.</li>
            </ul>
          </div>

          <div>
            <strong>3) Transfer Suggestions:</strong>
            <ul>
              <li>Consider upgrading Bentancur to a more attacking midfielder with better potential for points.</li>
              <li>Adding a premium midfielder or forward could boost overall point potential.</li>
              <li>Rotating goalkeepers to capitalize on favorable fixtures may also be beneficial.</li>
            </ul>
          </div>
        </div>
      )}
    </div>
  );
};

export default SquadResult;
