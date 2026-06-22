import { http } from './http'
import type { Client, DocumentType, Session } from '../types'

interface AuthResponse {
  token: string
  client: Pick<Client, 'id' | 'name' | 'documentId' | 'documentType' | 'planType' | 'active'>
}

export async function authenticate(documentId: string, documentType: DocumentType): Promise<Session> {
  const { data } = await http.post<AuthResponse>('/auth', { documentId, documentType })
  return { token: data.token, clientId: data.client.id, name: data.client.name }
}
