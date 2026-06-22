package com.bcb.bigchat.client.infrastructure;

import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    ClientRepository clientRepository;

    @Test
    void savesAndFindsClientByDocumentId() {
        Client client = new Client();
        client.setName("Test Client");
        client.setDocumentId("52998224725");
        client.setDocumentType(DocumentType.CPF);
        client.setPlanType(PlanType.PREPAID);
        client.setActive(true);
        clientRepository.save(client);

        Optional<Client> found = clientRepository.findByDocumentId("52998224725");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Client");
        assertThat(found.get().getDocumentType()).isEqualTo(DocumentType.CPF);
    }
}
