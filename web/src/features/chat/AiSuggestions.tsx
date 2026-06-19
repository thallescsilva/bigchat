interface Props {
  suggestions: string[]
  fallback: boolean
  loading: boolean
  error: string | null
  onPick: (suggestion: string) => void
  onDismiss: () => void
}

export function AiSuggestions({ suggestions, fallback, loading, error, onPick, onDismiss }: Props) {
  if (loading) return <div className="ai-suggestions ai-suggestions--state">Gerando sugestões...</div>
  if (error) return <div className="ai-suggestions ai-suggestions--state">{error}</div>
  if (suggestions.length === 0) return null

  return (
    <div className="ai-suggestions">
      <div className="ai-suggestions__header">
        <span>Sugestões{fallback ? ' (offline)' : ''}</span>
        <button type="button" className="link-button" onClick={onDismiss}>
          Fechar
        </button>
      </div>
      <div className="ai-suggestions__list">
        {suggestions.map((suggestion, index) => (
          <button
            key={index}
            type="button"
            className="ai-suggestions__chip"
            onClick={() => onPick(suggestion)}
          >
            {suggestion}
          </button>
        ))}
      </div>
    </div>
  )
}
