package com.bcb.bigchat.client.application;

import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.shared.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final DocumentValidator documentValidator;

    public ClientService(ClientRepository clientRepository, DocumentValidator documentValidator) {
        this.clientRepository = clientRepository;
        this.documentValidator = documentValidator;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + id));
    }

    public Client create(Client client) {
        documentValidator.validate(client.getDocumentId(), client.getDocumentType());
        String digits = client.getDocumentId().replaceAll("[^0-9]", "");
        client.setDocumentId(digits);
        return clientRepository.save(client);
    }

    public Client update(UUID id, Client updates) {
        Client client = findById(id);
        if (updates.getName() != null) client.setName(updates.getName());
        if (updates.getPlanType() != null) client.setPlanType(updates.getPlanType());
        if (updates.getMonthlyLimit() != null) client.setMonthlyLimit(updates.getMonthlyLimit());
        return clientRepository.save(client);
    }

    public void deactivate(UUID id) {
        Client client = findById(id);
        client.setActive(false);
        clientRepository.save(client);
    }
}
