package com.bcb.bigchat.auth.application;

import com.bcb.bigchat.auth.web.AuthResponse;
import com.bcb.bigchat.client.application.DocumentValidator;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.shared.error.ResourceNotFoundException;
import com.bcb.bigchat.shared.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock DocumentValidator documentValidator;
    @Mock JwtService jwtService;
    @InjectMocks AuthService authService;

    @Test
    void authenticatesValidClient() {
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setName("Test");
        client.setDocumentId("52998224725");
        when(clientRepository.findByDocumentId("52998224725")).thenReturn(Optional.of(client));
        when(jwtService.generateToken(client)).thenReturn("token123");

        AuthResponse response = authService.authenticate("52998224725", DocumentType.CPF);

        assertThat(response.token()).isEqualTo("token123");
        assertThat(response.clientId()).isEqualTo(client.getId());
    }

    @Test
    void throwsWhenClientNotFound() {
        when(clientRepository.findByDocumentId(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate("00000000000", DocumentType.CPF))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
