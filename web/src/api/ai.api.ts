import { http } from './http'
import type { AiSuggestions } from '../types'

export async function fetchSuggestions(conversationId: string): Promise<AiSuggestions> {
  const { data } = await http.post<AiSuggestions>(`/conversations/${conversationId}/ai-suggestions`)
  return data
}
