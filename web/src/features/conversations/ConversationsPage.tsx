import { useMemo, useState } from 'react'
import { useAuth } from '../../auth/useAuth'
import { BalanceBadge } from '../account/BalanceBadge'
import { ChatPage } from '../chat/ChatPage'
import type { ChatTarget } from '../chat/useMessages'
import { ConversationList } from './ConversationList'
import { NewConversation } from './NewConversation'
import { useConversations } from './useConversations'
import { Empty } from '../../components/Empty'
import type { Conversation } from '../../types'

type Selection =
  | { kind: 'existing'; conversation: Conversation }
  | { kind: 'draft'; recipientId: string }
  | null

export function ConversationsPage() {
  const { session, logout } = useAuth()
  const { conversations, loading, error, reload } = useConversations()
  const [selection, setSelection] = useState<Selection>(null)
  const [balanceSignal, setBalanceSignal] = useState(0)

  const selectedId = selection?.kind === 'existing' ? selection.conversation.id : null

  const target: ChatTarget | null = useMemo(() => {
    if (!selection) return null
    if (selection.kind === 'existing') {
      return { conversationId: selection.conversation.id, recipientId: selection.conversation.recipientId }
    }
    return { conversationId: null, recipientId: selection.recipientId }
  }, [selection])

  const title = selection
    ? selection.kind === 'existing'
      ? selection.conversation.recipientId
      : selection.recipientId
    : ''

  function startConversation(recipientId: string) {
    const existing = conversations.find((c) => c.recipientId === recipientId)
    setSelection(existing ? { kind: 'existing', conversation: existing } : { kind: 'draft', recipientId })
  }

  async function handleSent() {
    setBalanceSignal((n) => n + 1)
    const next = await reload()
    if (selection) {
      const recipientId = selection.kind === 'existing' ? selection.conversation.recipientId : selection.recipientId
      const fresh = next.find((c) => c.recipientId === recipientId)
      if (fresh) setSelection({ kind: 'existing', conversation: fresh })
    }
  }

  return (
    <div className="page">
      <header className="topbar">
        <strong>BigChat Brasil</strong>
        <div className="topbar__actions">
          {session && <BalanceBadge clientId={session.clientId} reloadSignal={balanceSignal} />}
          <span className="topbar__name">{session?.name}</span>
          <button className="link-button" onClick={logout}>
            Sair
          </button>
        </div>
      </header>

      <div className={`workspace${selection ? ' workspace--chat-open' : ''}`}>
        <aside className="sidebar">
          <NewConversation onStart={startConversation} />
          <ConversationList
            conversations={conversations}
            loading={loading}
            error={error}
            selectedId={selectedId}
            onSelect={(conversation) => setSelection({ kind: 'existing', conversation })}
          />
        </aside>

        <section className="chat-area">
          {target ? (
            <ChatPage target={target} title={title} onBack={() => setSelection(null)} onSent={handleSent} />
          ) : (
            <Empty message="Selecione ou inicie uma conversa." />
          )}
        </section>
      </div>
    </div>
  )
}
