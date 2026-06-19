import { describe, expect, it, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from '../../auth/AuthContext'
import { LoginPage } from './LoginPage'
import * as authApi from '../../api/auth.api'

function renderLogin() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <AuthProvider>
        <LoginPage />
      </AuthProvider>
    </MemoryRouter>,
  )
}

describe('LoginPage', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.restoreAllMocks()
  })

  it('keeps submit disabled until a valid document is typed', async () => {
    renderLogin()
    const button = screen.getByRole('button', { name: /entrar/i })
    expect(button).toBeDisabled()

    await userEvent.type(screen.getByLabelText(/cpf/i), '52998224725')
    expect(button).toBeEnabled()
    expect(screen.getByText(/CPF detectado/i)).toBeInTheDocument()
  })

  it('authenticates with sanitized digits and detected type', async () => {
    const spy = vi
      .spyOn(authApi, 'authenticate')
      .mockResolvedValue({ token: 't', clientId: 'c1', name: 'Maria' })

    renderLogin()
    await userEvent.type(screen.getByLabelText(/cpf/i), '52998224725')
    await userEvent.click(screen.getByRole('button', { name: /entrar/i }))

    await waitFor(() => expect(spy).toHaveBeenCalledWith('52998224725', 'CPF'))
  })

  it('shows an error message when authentication fails', async () => {
    vi.spyOn(authApi, 'authenticate').mockRejectedValue(new Error('boom'))

    renderLogin()
    await userEvent.type(screen.getByLabelText(/cpf/i), '52998224725')
    await userEvent.click(screen.getByRole('button', { name: /entrar/i }))

    expect(await screen.findByRole('alert')).toBeInTheDocument()
  })
})
