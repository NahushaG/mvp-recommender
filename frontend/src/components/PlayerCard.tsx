import React from 'react';

export interface Player {
  id: number;
  name: string;
  position: string;
  team: string;
  cost: number;
  totalPoints?: number;
  nextFixtures?: string[];
  recommendation?: string;
}

interface PlayerCardProps {
  player: Player;
  style?: React.CSSProperties; // allow parent to pass colors
}

const PlayerCard: React.FC<PlayerCardProps> = ({ player, style }) => {
  return (
    <div
      style={{
        border: '1px solid #ccc',
        borderRadius: '0.5rem',
        padding: '1rem',
        textAlign: 'center',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        backgroundColor: style?.backgroundColor || '#fff',
        color: '#333',
        transition: 'transform 0.2s, box-shadow 0.2s',
        position: 'relative',
        cursor: 'pointer',
      }}
    >
      <strong>{player.name}</strong>
      <p>{player.position}</p>
      <p>{player.team}</p>
      <p>Cost: {player.cost}m</p>

      {/* Hover details */}
      <div
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          padding: '0.5rem',
          borderRadius: '0.5rem',
          opacity: 0,
          pointerEvents: 'none',
          transition: 'opacity 0.2s',
        }}
        className="hover-overlay"
      >
        {player.totalPoints !== undefined && <p>Total Points: {player.totalPoints}</p>}
        {player.nextFixtures && (
          <div>
            <strong>Next Fixtures:</strong>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
              {player.nextFixtures.map((fixture, idx) => (
                <li key={idx}>{fixture}</li>
              ))}
            </ul>
          </div>
        )}
        {player.recommendation && <p>Recommendation: {player.recommendation}</p>}
      </div>

      <style>
        {`
        div:hover > .hover-overlay {
          opacity: 1;
          pointer-events: all;
        }
        div:hover {
          transform: translateY(-4px);
          box-shadow: 0 6px 12px rgba(0,0,0,0.2);
        }
      `}
      </style>
    </div>
  );
};

export default PlayerCard;
