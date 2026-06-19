package com.bcb.bigchat.client.infrastructure;

import com.bcb.bigchat.client.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByDocumentId(String documentId);
}
