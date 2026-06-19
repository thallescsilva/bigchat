import type { Conversation } from '../../types'
import { Spinner } from '../../components/Spinner'
import { ErrorBanner } from '../../components/ErrorBanner'
import { Empty } from '../../components/Empty'
import { ConversationListItem } from './ConversationListItem'

interface Props {
  conversations: Conversation[]
  loading: boolean
  error: string | null
  selectedId: string | null
  onSelect: (conversation: Conversation) => void
}

export function ConversationList({ conversations, loading, error, selectedId, onSelect }: Props) {
  if (loading) return <div className="conversation-list__state"><Spinner /></div>
  if (error) return <div className="conversation-list__state"><ErrorBanner message={error} /></div>
  if (conversations.length === 0) return <Empty message="Nenhuma conversa ainda." />

  return (
    <nav className="conversation-list">
      {conversations.map((conversation) => (
        <ConversationListItem
          key={conversation.id}
          conversation={conversation}
          active={conversation.id === selectedId}
          onSelect={onSelect}
        />
      ))}
    </nav>
  )
}
