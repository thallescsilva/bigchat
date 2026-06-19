import type { Message, MessageStatus } from '../../types'
import { formatBRL } from '../../lib/money'

const STATUS_LABEL: Record<MessageStatus, string> = {
  QUEUED: 'Na fila',
  PROCESSING: 'Processando',
  SENT: 'Enviada',
  FAILED: 'Falhou',
}

function statusModifier(status: MessageStatus): string {
  return `message-bubble__status--${status.toLowerCase()}`
}

export function MessageBubble({ message }: { message: Message }) {
  const time = new Date(message.createdAt).toLocaleTimeString('pt-BR', {
    hour: '2-digit',
    minute: '2-digit',
  })

  return (
    <div className={`message-bubble${message.priority === 'URGENT' ? ' message-bubble--urgent' : ''}`}>
      <p className="message-bubble__content">{message.content}</p>
      <div className="message-bubble__footer">
        <span>{time}</span>
        <span>{formatBRL(message.cost)}</span>
        {message.priority === 'URGENT' && <span className="message-bubble__urgent-tag">urgente</span>}
        <span className={`message-bubble__status ${statusModifier(message.status)}`}>
          {STATUS_LABEL[message.status]}
        </span>
      </div>
    </div>
  )
}
