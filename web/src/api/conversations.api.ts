import { http } from './http'
import type { Conversation, Message, Page } from '../types'

export async function listConversations(): Promise<Conversation[]> {
  const { data } = await http.get<Conversation[]>('/conversations')
  return data
}

export async function getMessages(conversationId: string): Promise<Message[]> {
  const { data } = await http.get<Page<Message>>(`/conversations/${conversationId}/messages`, {
    params: { page: 0, size: 100, sort: 'createdAt,asc' },
  })
  return data.content
}
