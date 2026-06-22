import { useCallback, useEffect, useState } from 'react'
import { listConversations } from '../../api/conversations.api'
import type { Conversation } from '../../types'

export function useConversations() {
  const [conversations, setConversations] = useState<Conversation[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const reload = useCallback(async (): Promise<Conversation[]> => {
    setLoading(true)
    setError(null)
    try {
      const next = await listConversations()
      setConversations(next)
      return next
    } catch {
      setError('Não foi possível carregar as conversas.')
      return []
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void reload()
  }, [reload])

  return { conversations, loading, error, reload }
}
