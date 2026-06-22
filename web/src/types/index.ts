export type DocumentType = 'CPF' | 'CNPJ'
export type PlanType = 'PREPAID' | 'POSTPAID'
export type MessageType = 'SMS' | 'WHATSAPP'
export type MessagePriority = 'NORMAL' | 'URGENT'
export type MessageStatus = 'QUEUED' | 'PROCESSING' | 'SENT' | 'FAILED'

export interface Session {
  token: string
  clientId: string
  name: string
}

export interface Client {
  id: string
  name: string
  documentId: string
  documentType: DocumentType
  planType: PlanType
  balance: number
  monthlyLimit: number
  monthlyUsage: number
  active: boolean
  admin: boolean
}

export interface Conversation {
  id: string
  clientId: string
  recipientId: string
  lastMessageAt: string | null
  unreadCount: number
}

export interface Message {
  id: string
  conversationId: string
  senderClientId: string
  recipientId: string
  content: string
  type: MessageType
  priority: MessagePriority
  status: MessageStatus
  cost: number
  createdAt: string
  processedAt: string | null
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface Transaction {
  id: string
  clientId: string
  messageId: string | null
  type: 'DEBIT' | 'CONSUMPTION' | 'ADJUSTMENT'
  amount: number
  balanceAfter: number
  description: string | null
  createdAt: string
}

export interface SendMessageResult {
  messageId: string
  status: string
  cost: number
  currentBalance: number | null
}

export interface AiSuggestions {
  suggestions: string[]
  fallback: boolean
}
