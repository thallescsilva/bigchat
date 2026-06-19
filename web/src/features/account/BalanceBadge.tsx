import { useEffect, useState } from 'react'
import { getClient } from '../../api/clients.api'
import { formatBRL } from '../../lib/money'
import type { Client } from '../../types'

interface BalanceBadgeProps {
  clientId: string
  reloadSignal?: number
}

export function BalanceBadge({ clientId, reloadSignal }: BalanceBadgeProps) {
  const [client, setClient] = useState<Client | null>(null)

  useEffect(() => {
    let active = true
    getClient(clientId)
      .then((c) => {
        if (active) setClient(c)
      })
      .catch(() => {
        if (active) setClient(null)
      })
    return () => {
      active = false
    }
  }, [clientId, reloadSignal])

  if (!client) return <span className="balance-badge balance-badge--loading">—</span>

  if (client.planType === 'PREPAID') {
    return (
      <span className="balance-badge" title="Saldo pré-pago">
        Saldo: <strong>{formatBRL(client.balance)}</strong>
      </span>
    )
  }

  const remaining = client.monthlyLimit - client.monthlyUsage
  return (
    <span className="balance-badge" title="Limite pós-pago (restante / total)">
      Limite: <strong>{formatBRL(remaining)}</strong> / {formatBRL(client.monthlyLimit)}
    </span>
  )
}
