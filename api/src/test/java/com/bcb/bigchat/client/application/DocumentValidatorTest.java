package com.bcb.bigchat.client.application;

import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.shared.error.InvalidDocumentException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DocumentValidatorTest {

    private final DocumentValidator validator = new DocumentValidator();

    @Test
    void validCpf() {
        assertThatNoException().isThrownBy(() -> validator.validate("529.982.247-25", DocumentType.CPF));
    }

    @Test
    void invalidCpfAllSameDigits() {
        assertThatThrownBy(() -> validator.validate("111.111.111-11", DocumentType.CPF))
                .isInstanceOf(InvalidDocumentException.class);
    }

    @Test
    void invalidCpfWrongCheckDigit() {
        assertThatThrownBy(() -> validator.validate("529.982.247-26", DocumentType.CPF))
                .isInstanceOf(InvalidDocumentException.class);
    }

    @Test
    void validCnpj() {
        assertThatNoException().isThrownBy(() -> validator.validate("11.222.333/0001-81", DocumentType.CNPJ));
    }

    @Test
    void invalidCnpjWrongCheckDigit() {
        assertThatThrownBy(() -> validator.validate("11.222.333/0001-82", DocumentType.CNPJ))
                .isInstanceOf(InvalidDocumentException.class);
    }
}
