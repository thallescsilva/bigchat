import { describe, expect, it, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BalanceBadge } from './BalanceBadge'
import * as clientsApi from '../../api/clients.api'
import type { Client } from '../../types'

function client(overrides: Partial<Client>): Client {
  return {
    id: 'c1',
    name: 'Maria',
    documentId: '52998224725',
    documentType: 'CPF',
    planType: 'PREPAID',
    balance: 0,
    monthlyLimit: 0,
    monthlyUsage: 0,
    active: true,
    admin: false,
    ...overrides,
  }
}

describe('BalanceBadge', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  it('shows the prepaid balance', async () => {
    vi.spyOn(clientsApi, 'getClient').mockResolvedValue(client({ planType: 'PREPAID', balance: 9.25 }))

    render(<BalanceBadge clientId="c1" />)

    expect(await screen.findByText(/Saldo:/i)).toBeInTheDocument()
    expect(screen.getByText(/R\$\s?9,25/)).toBeInTheDocument()
  })

  it('shows the postpaid remaining limit', async () => {
    vi.spyOn(clientsApi, 'getClient').mockResolvedValue(
      client({ planType: 'POSTPAID', monthlyLimit: 50, monthlyUsage: 12.5 }),
    )

    render(<BalanceBadge clientId="c1" />)

    expect(await screen.findByText(/Limite:/i)).toBeInTheDocument()
    expect(screen.getByText(/R\$\s?37,50/)).toBeInTheDocument()
  })
})
