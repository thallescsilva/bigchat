import { useCallback, useState } from 'react'
import { fetchSuggestions } from '../../api/ai.api'

export function useAiSuggestions(conversationId: string | null) {
  const [suggestions, setSuggestions] = useState<string[]>([])
  const [fallback, setFallback] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const generate = useCallback(async () => {
    if (!conversationId) return
    setLoading(true)
    setError(null)
    try {
      const result = await fetchSuggestions(conversationId)
      setSuggestions(result.suggestions)
      setFallback(result.fallback)
    } catch {
      setError('Não foi possível gerar sugestões agora.')
    } finally {
      setLoading(false)
    }
  }, [conversationId])

  const clear = useCallback(() => {
    setSuggestions([])
    setFallback(false)
    setError(null)
  }, [])

  return { suggestions, fallback, loading, error, generate, clear }
}
