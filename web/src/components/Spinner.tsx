export function Spinner({ label }: { label?: string }) {
  return (
    <span className="spinner" role="status" aria-live="polite">
      <span className="spinner__dot" aria-hidden />
      {label ?? 'Carregando...'}
    </span>
  )
}
