import { useState } from 'react'
import { useMessages, type ChatTarget } from './useMessages'
import { useAiSuggestions } from './useAiSuggestions'
import { MessageHistory } from './MessageHistory'
import { MessageComposer } from './MessageComposer'
import { AiSuggestions } from './AiSuggestions'

interface Props {
  target: ChatTarget
  title: string
  onBack?: () => void
  onSent: () => void
}

export function ChatPage({ target, title, onBack, onSent }: Props) {
  const { messages, loading, sending, error, send } = useMessages(target)
  const ai = useAiSuggestions(target.conversationId)
  const [draft, setDraft] = useState('')

  async function handleSend(input: { content: string; type: 'SMS' | 'WHATSAPP'; priority: 'NORMAL' | 'URGENT' }) {
    const result = await send(input)
    if (result) {
      setDraft('')
      ai.clear()
      onSent()
    }
  }

  return (
    <section className="chat">
      <header className="chat__header">
        {onBack && (
          <button type="button" className="link-button chat__back" onClick={onBack}>
            ← Voltar
          </button>
        )}
        <strong>{title}</strong>
      </header>

      <MessageHistory messages={messages} loading={loading} error={error} />

      <AiSuggestions
        suggestions={ai.suggestions}
        fallback={ai.fallback}
        loading={ai.loading}
        error={ai.error}
        onPick={(s) => setDraft(s)}
        onDismiss={ai.clear}
      />

      <MessageComposer
        content={draft}
        onContentChange={setDraft}
        onSend={handleSend}
        onRequestSuggestions={ai.generate}
        sending={sending}
        suggestionsLoading={ai.loading}
        canSuggest={Boolean(target.conversationId)}
      />
    </section>
  )
}
