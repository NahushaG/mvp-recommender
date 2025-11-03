cat <<EOT > README.md
# FPL Fantasy Squad Generator

A **Fantasy Premier League squad recommender** built with **React, TypeScript, and Vite**.  
Generate optimal squads based on budget, formation, and player preferences with AI-powered analysis.

---

## Features

- Generate squads using **custom budget, formation, and player filters**.
- **Must-have and excluded players** support.
- **AI Analysis** providing squad balance, weak areas, and transfer suggestions.
- Players displayed in **position-wise grouped cards** with color coding.
- Fully **responsive design** for desktop and mobile.
- Modern, interactive UI with **hover effects and shadow styling**.
- Built with **React + TypeScript + Vite** for fast development with HMR.

---

## Installation

\`\`\`bash
# Clone the repo
git clone https://github.com/yourusername/fpl-squad-generator.git
cd fpl-squad-generator

# Install dependencies
npm install
# or
yarn install

# Run development server
npm run dev
# or
yarn dev
\`\`\`

Open your browser at \`http://localhost:5173\` (default Vite port).

---

## Project Structure

\`\`\`
src/
├─ api/
│  └─ fplService.ts       # API calls and type definitions
├─ components/
│  ├─ PlayerCard.tsx      # Player card component
│  ├─ SquadResult.tsx     # Squad display component
├─ pages/
│  ├─ HomePage.tsx        # Landing page
│  └─ SquadGeneratorPage.tsx # Main squad generator
├─ App.tsx
├─ main.tsx
\`\`\`

---

## Form Inputs

- **Budget**: Total squad budget.
- **Formation**: Choose from presets like \`3-4-3\`, \`4-3-3\`, \`3-5-2\`, etc.
- **Must-Have Players**: Player IDs (comma-separated) you want in your squad.
- **Excluded Players**: Player IDs (comma-separated) to exclude from your squad.

---

## ESLint Configuration

This project uses **ESLint** with TypeScript and React-specific linting. For production, it’s recommended to enable **type-aware rules**.

\`\`\`ts
// eslint.config.js
import tseslint from '@rel1cx/eslint-config-ts';
import reactX from 'eslint-plugin-react-x';
import reactDom from 'eslint-plugin-react-dom';

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      tseslint.configs.recommendedTypeChecked,   // Type-checked recommended rules
      tseslint.configs.stylisticTypeChecked,     // Stylistic rules
      reactX.configs['recommended-typescript'],  // React TypeScript rules
      reactDom.configs.recommended,              // React DOM rules
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
    },
  },
]);
\`\`\`

---

## Recommended Plugins

- **[eslint-plugin-react-x](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-x)** – TypeScript React linting.
- **[eslint-plugin-react-dom](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-dom)** – DOM-specific React linting.

---

## Vite Plugins

- **[@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react)** – Uses Babel for Fast Refresh.
- **[@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react-swc)** – Uses SWC for Fast Refresh (faster build).

> The React Compiler is not enabled by default. See [React Compiler Docs](https://react.dev/learn/react-compiler/installation) if you want to enable it.

---

## Development Notes

- **Responsive UI** uses CSS grid and flexbox for alignment.
- **Player cards** are grouped by positions: Goalkeeper, Defender, Midfielder, Forward.
- **Formation visualization** is grouped by position; future updates may include a football pitch layout.
- **Color coding** is used for different positions for better readability.
- API responses are mapped to frontend-friendly \`Player\` objects for rendering.

---

## Future Enhancements
- Test case 
- More dynamic layout and option
- Real **football pitch visualization** for squad layout.
- Interactive **drag-and-drop** squad builder.
- Enhanced AI suggestions for **weekly transfers**.

👤 Author
Nahusha G GitHub: NahushaG
