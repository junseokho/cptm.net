import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import Layout from './components/MainLayout.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
      <>
          <Layout/>
      </>
  </StrictMode>,
)
