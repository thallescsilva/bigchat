import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { AxiosError } from 'axios'
import { useAuth } from '../../auth/useAuth'
import { useDocumentInput } from './useDocumentInput'
import { Button } from '../../components/Button'
import { ErrorBanner } from '../../components/ErrorBanner'

function describeError(err: unknown): string {
  if (err instanceof AxiosError) {
    if (err.response?.status === 404) return 'Documento não encontrado. Verifique o número informado.'
    if (err.response?.status === 400) return 'Documento inválido.'
    if (!err.response) return 'Não foi possível conectar ao servidor.'
  }
  return 'Falha ao entrar. Tente novamente.'
}

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const doc = useDocumentInput()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const redirectTo = (location.state as { from?: string } | null)?.from ?? '/conversations'

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    if (!doc.valid || !doc.type) return
    setLoading(true)
    setError(null)
    try {
      await login(doc.digits, doc.type)
      navigate(redirectTo, { replace: true })
    } catch (err) {
      setError(describeError(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="login">
      <form className="login__card" onSubmit={handleSubmit}>
        <h1>BigChat Brasil</h1>
        <p className="login__subtitle">Entre com seu CPF ou CNPJ</p>

        <label htmlFor="document">CPF / CNPJ</label>
        <input
          id="document"
          name="document"
          inputMode="numeric"
          autoComplete="off"
          placeholder="000.000.000-00"
          value={doc.value}
          onChange={(e) => doc.onChange(e.target.value)}
          aria-invalid={doc.value.length > 0 && !doc.valid}
        />
        {doc.type && <span className="login__hint">{doc.type} detectado</span>}

        {error && <ErrorBanner message={error} />}

        <Button type="submit" loading={loading} disabled={!doc.valid}>
          Entrar
        </Button>
      </form>
    </main>
  )
}
