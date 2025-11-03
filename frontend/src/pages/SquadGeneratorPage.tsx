import React, { useState, type FormEvent } from 'react';
import { generateSquad, type ApiSquadResult } from '../api/fplService';
import SquadResult from '../components/SquadResult';
import type { Player } from '../components/PlayerCard';

const formations = ['3-4-3', '4-3-3', '3-5-2', '5-5-3', '4-4-2', '5-3-2'];

const SquadGeneratorPage: React.FC = () => {
  const [budget, setBudget] = useState<number>(100);
  const [formation, setFormation] = useState<string>('3-4-3');
  const [mustHave, setMustHave] = useState<string>('');
  const [excluded, setExcluded] = useState<string>('');
  const [result, setResult] = useState<{ players: Player[]; analysis?: string } | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const payload = {
        budget,
        formation,
        mustHavePlayers: mustHave
          .split(',')
          .map((id) => parseInt(id.trim()))
          .filter(Boolean),
        excludedPlayers: excluded
          .split(',')
          .map((id) => parseInt(id.trim()))
          .filter(Boolean),
      };

      const data: ApiSquadResult = await generateSquad(payload);

      const mappedPlayers: Player[] = data.selectedPlayers.map((p) => ({
        id: p.playerId,
        name: p.name,
        position: p.position,
        team: p.team,
        cost: p.price,
      }));

      setResult({ players: mappedPlayers, analysis: data.aiAnalysis });
    } catch (err) {
      console.error(err);
      setError('Failed to generate squad');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        width: '100%',
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '2rem',
        fontFamily: 'Arial, sans-serif',
      }}
    >
      <h2 style={{ textAlign: 'center', color: '#2a9d8f', marginBottom: '2rem' }}>
        Fantasy Squad Generator
      </h2>

      <form
        onSubmit={handleSubmit}
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
          gap: '1rem',
          backgroundColor: '#f1faee',
          padding: '2rem',
          borderRadius: '12px',
          boxShadow: '0 6px 10px rgba(0,0,0,0.1)',
          marginBottom: '2rem',
        }}
      >
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          <label style={{ marginBottom: '0.5rem', fontWeight: 'bold' }}>Budget:</label>
          <input
            type="number"
            value={budget}
            onChange={(e) => setBudget(Number(e.target.value))}
            style={{ padding: '0.5rem', borderRadius: '6px', border: '1px solid #ccc' }}
          />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column' }}>
          <label style={{ marginBottom: '0.5rem', fontWeight: 'bold' }}>Formation:</label>
          <select
            value={formation}
            onChange={(e) => setFormation(e.target.value)}
            style={{ padding: '0.5rem', borderRadius: '6px', border: '1px solid #ccc' }}
          >
            {formations.map((f) => (
              <option key={f} value={f}>
                {f}
              </option>
            ))}
          </select>
        </div>

        <div style={{ gridColumn: '1 / 3', display: 'flex', flexDirection: 'column' }}>
          <label style={{ marginBottom: '0.5rem', fontWeight: 'bold' }}>
            Must-Have Players (IDs, comma separated):
          </label>
          <input
            type="text"
            value={mustHave}
            onChange={(e) => setMustHave(e.target.value)}
            style={{ padding: '0.5rem', borderRadius: '6px', border: '1px solid #ccc' }}
          />
        </div>

        <div style={{ gridColumn: '1 / 3', display: 'flex', flexDirection: 'column' }}>
          <label style={{ marginBottom: '0.5rem', fontWeight: 'bold' }}>
            Excluded Players (IDs, comma separated):
          </label>
          <input
            type="text"
            value={excluded}
            onChange={(e) => setExcluded(e.target.value)}
            style={{ padding: '0.5rem', borderRadius: '6px', border: '1px solid #ccc' }}
          />
        </div>

        <div style={{ gridColumn: '1 / 3', textAlign: 'center' }}>
          <button
            type="submit"
            disabled={loading}
            style={{
              padding: '0.75rem 2rem',
              backgroundColor: '#2a9d8f',
              color: '#fff',
              fontWeight: 'bold',
              border: 'none',
              borderRadius: '8px',
              cursor: 'pointer',
              transition: '0.2s transform',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.05)')}
            onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
          >
            {loading ? 'Generating...' : 'Generate Squad'}
          </button>
        </div>
      </form>

      {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}

      {result?.players && <SquadResult players={result.players} analysis={result.analysis} />}
    </div>
  );
};

export default SquadGeneratorPage;
