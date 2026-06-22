import { http } from './http'
import type { Client } from '../types'

export async function getClient(clientId: string): Promise<Client> {
  const { data } = await http.get<Client>(`/clients/${clientId}`)
  return data
}
