import { http } from './http'
import type { DocumentType, Session } from '../types'

interface AuthResponse {
  token: string
  clientId: string
  name: string
}

export async function authenticate(documentId: string, documentType: DocumentType): Promise<Session> {
  const { data } = await http.post<AuthResponse>('/auth', { documentId, documentType })
  return { token: data.token, clientId: data.clientId, name: data.name }
}
