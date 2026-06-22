import { http } from './http'
import type { MessagePriority, MessageType, SendMessageResult } from '../types'

export interface SendMessagePayload {
  recipientId: string
  content: string
  type: MessageType
  priority: MessagePriority
}

export async function sendMessage(payload: SendMessagePayload): Promise<SendMessageResult> {
  const { data } = await http.post<SendMessageResult>('/messages', payload)
  return data
}
