import { useEffect, useRef } from 'react'
import type { Message } from '../../types'
import { Spinner } from '../../components/Spinner'
import { ErrorBanner } from '../../components/ErrorBanner'
import { Empty } from '../../components/Empty'
import { MessageBubble } from './MessageBubble'

interface Props {
  messages: Message[]
  loading: boolean
  error: string | null
}

export function MessageHistory({ messages, loading, error }: Props) {
  const endRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  if (loading && messages.length === 0) {
    return <div className="message-history message-history--state"><Spinner /></div>
  }
  if (error) {
    return <div className="message-history message-history--state"><ErrorBanner message={error} /></div>
  }
  if (messages.length === 0) {
    return <Empty message="Envie a primeira mensagem desta conversa." />
  }

  return (
    <div className="message-history">
      {messages.map((message) => (
        <MessageBubble key={message.id} message={message} />
      ))}
      <div ref={endRef} />
    </div>
  )
}
