import { Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from './auth/ProtectedRoute'
import { LoginPage } from './features/login/LoginPage'
import { ConversationsPage } from './features/conversations/ConversationsPage'

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/conversations" element={<ConversationsPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/conversations" replace />} />
    </Routes>
  )
}
