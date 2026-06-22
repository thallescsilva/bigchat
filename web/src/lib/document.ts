import type { DocumentType } from '../types'

export function onlyDigits(value: string): string {
  return value.replace(/\D/g, '')
}

export function detectDocumentType(value: string): DocumentType | null {
  const digits = onlyDigits(value)
  if (digits.length === 11) return 'CPF'
  if (digits.length === 14) return 'CNPJ'
  return null
}

export function maskDocument(value: string): string {
  const digits = onlyDigits(value).slice(0, 14)
  if (digits.length <= 11) {
    return digits
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
  }
  return digits
    .replace(/(\d{2})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1/$2')
    .replace(/(\d{4})(\d{1,2})$/, '$1-$2')
}

export function isValidCPF(value: string): boolean {
  const cpf = onlyDigits(value)
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false

  const digit = (length: number): number => {
    let sum = 0
    for (let i = 0; i < length; i++) {
      sum += Number(cpf[i]) * (length + 1 - i)
    }
    const result = (sum * 10) % 11
    return result === 10 ? 0 : result
  }

  return digit(9) === Number(cpf[9]) && digit(10) === Number(cpf[10])
}

export function isValidCNPJ(value: string): boolean {
  const cnpj = onlyDigits(value)
  if (cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj)) return false

  const digit = (length: number): number => {
    const weights = length === 12 ? [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2] : [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
    let sum = 0
    for (let i = 0; i < length; i++) {
      sum += Number(cnpj[i]) * weights[i]
    }
    const result = sum % 11
    return result < 2 ? 0 : 11 - result
  }

  return digit(12) === Number(cnpj[12]) && digit(13) === Number(cnpj[13])
}

export function isValidDocument(value: string): boolean {
  const type = detectDocumentType(value)
  if (type === 'CPF') return isValidCPF(value)
  if (type === 'CNPJ') return isValidCNPJ(value)
  return false
}
