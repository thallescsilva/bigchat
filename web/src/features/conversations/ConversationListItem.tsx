import type { Conversation } from '../../types'

interface Props {
  conversation: Conversation
  active: boolean
  onSelect: (conversation: Conversation) => void
}

function formatWhen(value: string | null): string {
  if (!value) return ''
  const date = new Date(value)
  return date.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' })
}

export function ConversationListItem({ conversation, active, onSelect }: Props) {
  return (
    <button
      type="button"
      className={`conversation-item${active ? ' conversation-item--active' : ''}`}
      onClick={() => onSelect(conversation)}
    >
      <span className="conversation-item__recipient">{conversation.recipientName}</span>
      {conversation.lastMessageContent && (
        <span className="conversation-item__preview">{conversation.lastMessageContent}</span>
      )}
      <span className="conversation-item__meta">
        {formatWhen(conversation.lastMessageTime)}
        {conversation.unreadCount > 0 && (
          <span className="conversation-item__badge">{conversation.unreadCount}</span>
        )}
      </span>
    </button>
  )
}
