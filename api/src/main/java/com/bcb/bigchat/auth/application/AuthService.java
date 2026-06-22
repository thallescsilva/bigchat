package com.bcb.bigchat.auth.application;

import com.bcb.bigchat.auth.web.AuthResponse;
import com.bcb.bigchat.client.application.DocumentValidator;
import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.shared.error.ResourceNotFoundException;
import com.bcb.bigchat.shared.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final ClientRepository clientRepository;
    private final DocumentValidator documentValidator;
    private final JwtService jwtService;

    public AuthService(ClientRepository clientRepository, DocumentValidator documentValidator, JwtService jwtService) {
        this.clientRepository = clientRepository;
        this.documentValidator = documentValidator;
        this.jwtService = jwtService;
    }

    public AuthResponse authenticate(String documentId, DocumentType documentType) {
        var client = clientRepository.findByDocumentId(documentId.replaceAll("[^0-9]", ""))
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        documentValidator.validate(documentId, documentType);
        String token = jwtService.generateToken(client);
        return new AuthResponse(token, client.getId(), client.getName());
    }
}
