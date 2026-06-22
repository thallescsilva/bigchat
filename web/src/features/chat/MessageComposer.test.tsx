import { describe, expect, it, vi } from 'vitest'
import { useState } from 'react'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MessageComposer } from './MessageComposer'
import type { MessagePriority, MessageType } from '../../types'

interface HarnessProps {
  onSend: (input: { content: string; type: MessageType; priority: MessagePriority }) => void
  canSuggest?: boolean
  onRequestSuggestions?: () => void
}

function Harness({ onSend, canSuggest = true, onRequestSuggestions = () => {} }: HarnessProps) {
  const [content, setContent] = useState('')
  return (
    <MessageComposer
      content={content}
      onContentChange={setContent}
      onSend={onSend}
      onRequestSuggestions={onRequestSuggestions}
      sending={false}
      suggestionsLoading={false}
      canSuggest={canSuggest}
    />
  )
}

describe('MessageComposer', () => {
  it('sends trimmed content with selected type and priority', async () => {
    const onSend = vi.fn()
    render(<Harness onSend={onSend} />)

    await userEvent.type(screen.getByLabelText(/mensagem/i), '  Olá mundo  ')
    await userEvent.selectOptions(screen.getByLabelText(/prioridade/i), 'URGENT')
    await userEvent.selectOptions(screen.getByLabelText(/tipo/i), 'WHATSAPP')
    await userEvent.click(screen.getByRole('button', { name: /enviar/i }))

    expect(onSend).toHaveBeenCalledWith({ content: 'Olá mundo', type: 'WHATSAPP', priority: 'URGENT' })
  })

  it('disables send for empty content', () => {
    render(<Harness onSend={vi.fn()} />)
    expect(screen.getByRole('button', { name: /enviar/i })).toBeDisabled()
  })

  it('disables the suggest button when suggestions are not available', () => {
    render(<Harness onSend={vi.fn()} canSuggest={false} />)
    expect(screen.getByRole('button', { name: /sugerir resposta/i })).toBeDisabled()
  })

  it('requests suggestions when enabled', async () => {
    const onRequestSuggestions = vi.fn()
    render(<Harness onSend={vi.fn()} onRequestSuggestions={onRequestSuggestions} />)

    await userEvent.click(screen.getByRole('button', { name: /sugerir resposta/i }))
    expect(onRequestSuggestions).toHaveBeenCalled()
  })
})
