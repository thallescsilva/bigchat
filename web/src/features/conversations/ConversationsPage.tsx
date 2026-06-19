import { useAuth } from '../../auth/useAuth'

export function ConversationsPage() {
  const { session, logout } = useAuth()

  return (
    <div className="page">
      <header className="topbar">
        <strong>BigChat Brasil</strong>
        <div className="topbar__actions">
          <span>{session?.name}</span>
          <button className="link-button" onClick={logout}>
            Sair
          </button>
        </div>
      </header>
      <main style={{ padding: '1.5rem' }}>
        <p>Conversas chegam na Fase 8.</p>
      </main>
    </div>
  )
}
