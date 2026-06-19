import { useCallback, useEffect, useState } from 'react'
import { getMessages } from '../../api/conversations.api'
import { sendMessage } from '../../api/messages.api'
import type { Message, MessagePriority, MessageType, SendMessageResult } from '../../types'

export interface ChatTarget {
  conversationId: string | null
  recipientId: string
}

export interface SendInput {
  content: string
  type: MessageType
  priority: MessagePriority
}

export function useMessages(target: ChatTarget | null) {
  const [messages, setMessages] = useState<Message[]>([])
  const [loading, setLoading] = useState(false)
  const [sending, setSending] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const conversationId = target?.conversationId ?? null

  const reload = useCallback(async () => {
    if (!conversationId) {
      setMessages([])
      return
    }
    setLoading(true)
    setError(null)
    try {
      setMessages(await getMessages(conversationId))
    } catch {
      setError('Não foi possível carregar as mensagens.')
    } finally {
      setLoading(false)
    }
  }, [conversationId])

  useEffect(() => {
    void reload()
  }, [reload])

  const send = useCallback(
    async (input: SendInput): Promise<SendMessageResult | null> => {
      if (!target) return null
      setSending(true)
      setError(null)
      try {
        const result = await sendMessage({ recipientId: target.recipientId, ...input })
        await reload()
        return result
      } catch {
        setError('Falha ao enviar a mensagem. Verifique saldo/limite.')
        return null
      } finally {
        setSending(false)
      }
    },
    [target, reload],
  )

  return { messages, loading, sending, error, send, reload }
}
