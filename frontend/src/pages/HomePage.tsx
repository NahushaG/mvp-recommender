import React from 'react';
import { Link } from 'react-router-dom';

const HomePage: React.FC = () => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '90vh',
        backgroundColor: '#f1faee',
        fontFamily: 'Arial, sans-serif',
        padding: '2rem',
        textAlign: 'center',
      }}
    >
      <h1 style={{ color: '#2a9d8f', fontSize: '3rem', marginBottom: '1rem' }}>
        MVP Recommender
      </h1>
      <p style={{ fontSize: '1.25rem', color: '#264653', maxWidth: '600px', marginBottom: '2rem' }}>
        Welcome to the Fantasy Premier League Squad Recommender! Build the perfect squad and maximize your points.
      </p>
      <Link
        to="/squad"
        style={{
          backgroundColor: '#e76f51',
          color: '#fff',
          padding: '1rem 2rem',
          borderRadius: '8px',
          textDecoration: 'none',
          fontWeight: 'bold',
          boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
          transition: 'transform 0.2s',
        }}
        onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.05)')}
        onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      >
        Generate Your Squad
      </Link>
    </div>
  );
};

export default HomePage;
