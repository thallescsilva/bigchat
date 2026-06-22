import { describe, expect, it } from 'vitest'
import {
  detectDocumentType,
  isValidCNPJ,
  isValidCPF,
  isValidDocument,
  maskDocument,
} from './document'

describe('detectDocumentType', () => {
  it('detects CPF by 11 digits', () => {
    expect(detectDocumentType('529.982.247-25')).toBe('CPF')
  })

  it('detects CNPJ by 14 digits', () => {
    expect(detectDocumentType('11.222.333/0001-81')).toBe('CNPJ')
  })

  it('returns null for incomplete input', () => {
    expect(detectDocumentType('123')).toBeNull()
  })
})

describe('isValidCPF', () => {
  it('accepts a valid CPF', () => {
    expect(isValidCPF('529.982.247-25')).toBe(true)
  })

  it('rejects an invalid check digit', () => {
    expect(isValidCPF('529.982.247-26')).toBe(false)
  })

  it('rejects repeated digits', () => {
    expect(isValidCPF('111.111.111-11')).toBe(false)
  })
})

describe('isValidCNPJ', () => {
  it('accepts a valid CNPJ', () => {
    expect(isValidCNPJ('11.222.333/0001-81')).toBe(true)
  })

  it('rejects an invalid check digit', () => {
    expect(isValidCNPJ('11.222.333/0001-80')).toBe(false)
  })
})

describe('isValidDocument', () => {
  it('validates both CPF and CNPJ', () => {
    expect(isValidDocument('52998224725')).toBe(true)
    expect(isValidDocument('11222333000181')).toBe(true)
    expect(isValidDocument('12345')).toBe(false)
  })
})

describe('maskDocument', () => {
  it('masks a CPF', () => {
    expect(maskDocument('52998224725')).toBe('529.982.247-25')
  })

  it('masks a CNPJ', () => {
    expect(maskDocument('11222333000181')).toBe('11.222.333/0001-81')
  })
})
