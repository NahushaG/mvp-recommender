import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import HomePage from './pages/HomePage';
import SquadGeneratorPage from './pages/SquadGeneratorPage';

const App: React.FC = () => {
  return (
    <Router>
      <header style={{ padding: '1rem', borderBottom: '1px solid #ccc' }}>
        <Link to="/" style={{ marginRight: '1rem' }}>Home</Link>
        <Link to="/squad">Generate Squad</Link>
      </header>

      <main style={{ padding: '1rem' }}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/squad" element={<SquadGeneratorPage />} />
        </Routes>
      </main>
    </Router>
  );
};

export default App;
