import { useState } from 'react'
import type { MessagePriority, MessageType } from '../../types'
import { Button } from '../../components/Button'

interface Props {
  content: string
  onContentChange: (value: string) => void
  onSend: (input: { content: string; type: MessageType; priority: MessagePriority }) => void
  onRequestSuggestions: () => void
  sending: boolean
  suggestionsLoading: boolean
  canSuggest: boolean
  disabled?: boolean
}

export function MessageComposer({
  content,
  onContentChange,
  onSend,
  onRequestSuggestions,
  sending,
  suggestionsLoading,
  canSuggest,
  disabled,
}: Props) {
  const [type, setType] = useState<MessageType>('SMS')
  const [priority, setPriority] = useState<MessagePriority>('NORMAL')

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    const trimmed = content.trim()
    if (!trimmed) return
    onSend({ content: trimmed, type, priority })
  }

  return (
    <form className="composer" onSubmit={handleSubmit}>
      <div className="composer__options">
        <label>
          Tipo
          <select value={type} onChange={(e) => setType(e.target.value as MessageType)}>
            <option value="SMS">SMS</option>
            <option value="WHATSAPP">WhatsApp</option>
          </select>
        </label>
        <label>
          Prioridade
          <select value={priority} onChange={(e) => setPriority(e.target.value as MessagePriority)}>
            <option value="NORMAL">Normal (R$ 0,25)</option>
            <option value="URGENT">Urgente (R$ 0,50)</option>
          </select>
        </label>
        <button
          type="button"
          className="composer__suggest"
          onClick={onRequestSuggestions}
          disabled={disabled || suggestionsLoading || !canSuggest}
          title={canSuggest ? 'Gerar sugestões a partir do histórico' : 'Envie a primeira mensagem para habilitar as sugestões'}
        >
          {suggestionsLoading ? 'Sugerindo...' : '✨ Sugerir resposta'}
        </button>
      </div>
      <div className="composer__row">
        <textarea
          aria-label="Mensagem"
          placeholder="Escreva sua mensagem..."
          value={content}
          onChange={(e) => onContentChange(e.target.value)}
          rows={2}
          disabled={disabled}
        />
        <Button type="submit" loading={sending} disabled={disabled || !content.trim()}>
          Enviar
        </Button>
      </div>
    </form>
  )
}
