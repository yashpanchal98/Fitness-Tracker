import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { AuthProvider } from 'react-oidc-context'
import App from './App.jsx'
import './index.css'

const oidcConfig = {
  authority: "http://localhost:8181/realms/fitness-oauth2",
  client_id: "oauth2-pkce-client",
  redirect_uri: "http://localhost:5173",
  response_type: "code",
  scope: "openid profile email",
  onSigninCallback: (_user) => {
    window.history.replaceState(
      {},
      document.title,
      window.location.pathname
    )
  }
};

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </StrictMode>,
)
