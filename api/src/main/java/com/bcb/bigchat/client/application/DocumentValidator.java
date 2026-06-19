package com.bcb.bigchat.client.application;

import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.shared.error.InvalidDocumentException;
import org.springframework.stereotype.Component;

@Component
public class DocumentValidator {

    public void validate(String documentId, DocumentType type) {
        String digits = documentId.replaceAll("[^0-9]", "");
        if (type == DocumentType.CPF) {
            validateCpf(digits);
        } else {
            validateCnpj(digits);
        }
    }

    private void validateCpf(String digits) {
        if (digits.length() != 11 || digits.chars().distinct().count() == 1) {
            throw new InvalidDocumentException("Invalid CPF: " + digits);
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (digits.charAt(i) - '0') * (10 - i);
        int r1 = (sum * 10) % 11;
        if (r1 == 10) r1 = 0;
        if (r1 != (digits.charAt(9) - '0')) throw new InvalidDocumentException("Invalid CPF check digit");

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (digits.charAt(i) - '0') * (11 - i);
        int r2 = (sum * 10) % 11;
        if (r2 == 10) r2 = 0;
        if (r2 != (digits.charAt(10) - '0')) throw new InvalidDocumentException("Invalid CPF check digit");
    }

    private void validateCnpj(String digits) {
        if (digits.length() != 14 || digits.chars().distinct().count() == 1) {
            throw new InvalidDocumentException("Invalid CNPJ: " + digits);
        }
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) sum += (digits.charAt(i) - '0') * weights1[i];
        int r1 = sum % 11;
        r1 = r1 < 2 ? 0 : 11 - r1;
        if (r1 != (digits.charAt(12) - '0')) throw new InvalidDocumentException("Invalid CNPJ check digit");

        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) sum += (digits.charAt(i) - '0') * weights2[i];
        int r2 = sum % 11;
        r2 = r2 < 2 ? 0 : 11 - r2;
        if (r2 != (digits.charAt(13) - '0')) throw new InvalidDocumentException("Invalid CNPJ check digit");
    }
}
