import { useState } from 'react'

export function NewConversation({ onStart }: { onStart: (recipientId: string) => void }) {
  const [recipient, setRecipient] = useState('')

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    const trimmed = recipient.trim()
    if (!trimmed) return
    onStart(trimmed)
    setRecipient('')
  }

  return (
    <form className="new-conversation" onSubmit={handleSubmit}>
      <input
        aria-label="Destinatário"
        placeholder="Novo destinatário..."
        value={recipient}
        onChange={(e) => setRecipient(e.target.value)}
      />
      <button type="submit" className="new-conversation__button" disabled={!recipient.trim()}>
        +
      </button>
    </form>
  )
}
