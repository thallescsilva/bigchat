import { useMemo, useState } from 'react'
import { detectDocumentType, isValidDocument, maskDocument, onlyDigits } from '../../lib/document'
import type { DocumentType } from '../../types'

interface DocumentInput {
  value: string
  digits: string
  type: DocumentType | null
  valid: boolean
  onChange: (raw: string) => void
}

export function useDocumentInput(): DocumentInput {
  const [value, setValue] = useState('')

  const digits = onlyDigits(value)
  const type = useMemo(() => detectDocumentType(value), [value])
  const valid = useMemo(() => isValidDocument(value), [value])

  const onChange = (raw: string) => setValue(maskDocument(raw))

  return { value, digits, type, valid, onChange }
}
