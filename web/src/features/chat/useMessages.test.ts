import { describe, expect, it, vi, beforeEach } from 'vitest'
import { renderHook, waitFor, act } from '@testing-library/react'
import { useMessages } from './useMessages'
import * as conversationsApi from '../../api/conversations.api'
import * as messagesApi from '../../api/messages.api'
import type { Message } from '../../types'

function message(id: string, content: string): Message {
  return {
    id,
    conversationId: 'conv-1',
    senderClientId: 'client-1',
    recipientId: 'recipient-1',
    content,
    type: 'SMS',
    priority: 'NORMAL',
    status: 'SENT',
    cost: 0.25,
    createdAt: '2026-06-19T12:00:00',
    processedAt: '2026-06-19T12:00:01',
  }
}

describe('useMessages', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  it('loads messages for the selected conversation', async () => {
    vi.spyOn(conversationsApi, 'getMessages').mockResolvedValue([message('m1', 'Olá')])

    const { result } = renderHook(() =>
      useMessages({ conversationId: 'conv-1', recipientId: 'recipient-1' }),
    )

    await waitFor(() => expect(result.current.messages).toHaveLength(1))
    expect(conversationsApi.getMessages).toHaveBeenCalledWith('conv-1')
  })

  it('does not fetch when there is no conversation id (draft)', async () => {
    const spy = vi.spyOn(conversationsApi, 'getMessages').mockResolvedValue([])

    const { result } = renderHook(() => useMessages({ conversationId: null, recipientId: 'new-recipient' }))

    await waitFor(() => expect(result.current.loading).toBe(false))
    expect(spy).not.toHaveBeenCalled()
    expect(result.current.messages).toEqual([])
  })

  it('sends with the target recipient and reloads', async () => {
    vi.spyOn(conversationsApi, 'getMessages').mockResolvedValue([message('m1', 'Oi')])
    const sendSpy = vi
      .spyOn(messagesApi, 'sendMessage')
      .mockResolvedValue({ messageId: 'm2', status: 'queued', cost: 0.5, currentBalance: 9.5 })

    const { result } = renderHook(() =>
      useMessages({ conversationId: 'conv-1', recipientId: 'recipient-1' }),
    )
    await waitFor(() => expect(result.current.messages).toHaveLength(1))

    await act(async () => {
      await result.current.send({ content: 'Urgente', type: 'SMS', priority: 'URGENT' })
    })

    expect(sendSpy).toHaveBeenCalledWith({
      recipientId: 'recipient-1',
      content: 'Urgente',
      type: 'SMS',
      priority: 'URGENT',
    })
    expect(conversationsApi.getMessages).toHaveBeenCalledTimes(2)
  })

  it('surfaces an error when send fails', async () => {
    vi.spyOn(conversationsApi, 'getMessages').mockResolvedValue([])
    vi.spyOn(messagesApi, 'sendMessage').mockRejectedValue(new Error('no balance'))

    const { result } = renderHook(() =>
      useMessages({ conversationId: 'conv-1', recipientId: 'recipient-1' }),
    )
    await waitFor(() => expect(result.current.loading).toBe(false))

    await act(async () => {
      const res = await result.current.send({ content: 'x', type: 'SMS', priority: 'NORMAL' })
      expect(res).toBeNull()
    })

    expect(result.current.error).not.toBeNull()
  })
})
