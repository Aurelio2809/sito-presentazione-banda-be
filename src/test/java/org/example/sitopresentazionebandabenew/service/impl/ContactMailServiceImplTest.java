package org.example.sitopresentazionebandabenew.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.example.sitopresentazionebandabenew.dto.requests.MessageRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class ContactMailServiceImplTest {

    @Test
    void sendsContactMessageToAssociationMailboxWithoutPersistence() {
        JavaMailSender mailSender = org.mockito.Mockito.mock(JavaMailSender.class);
        ContactMailServiceImpl service = new ContactMailServiceImpl(
                mailSender, "bandamusicalecasalidelmanco@gmail.com", "bandamusicalecasalidelmanco@gmail.com");
        MessageRequest request = new MessageRequest(
                "Mario Rossi", "mario@example.it", "Informazioni\r\nBcc: attacker@example.it", "Buongiorno");

        service.send(request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).containsExactly("bandamusicalecasalidelmanco@gmail.com");
        assertThat(sent.getReplyTo()).isEqualTo("mario@example.it");
        assertThat(sent.getSubject()).doesNotContain("\r", "\n");
        assertThat(sent.getText()).contains("Mario Rossi", "Buongiorno");
    }
}
