import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import Main from './components/Main.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
      <>
          <Main/>
      </>
  </StrictMode>,
)
