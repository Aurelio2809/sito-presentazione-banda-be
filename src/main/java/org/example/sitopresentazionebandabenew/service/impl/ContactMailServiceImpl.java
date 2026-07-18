package org.example.sitopresentazionebandabenew.service.impl;

import org.example.sitopresentazionebandabenew.dto.requests.MessageRequest;
import org.example.sitopresentazionebandabenew.exception.ContactMailException;
import org.example.sitopresentazionebandabenew.service.ContactMailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ContactMailServiceImpl implements ContactMailService {

    private final JavaMailSender mailSender;
    private final String destination;
    private final String sender;

    public ContactMailServiceImpl(
            JavaMailSender mailSender,
            @Value("${app.contact.mail-to}") String destination,
            @Value("${spring.mail.username}") String sender) {
        this.mailSender = mailSender;
        this.destination = destination;
        this.sender = sender;
    }

    @Override
    public void send(MessageRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(destination);
        message.setReplyTo(sanitizeHeader(request.getSenderEmail()));
        message.setSubject("[Sito Banda] " + sanitizeHeader(request.getSubject()));
        message.setText("Nome: " + request.getSenderName().trim() + "\nEmail: "
                + request.getSenderEmail().trim() + "\n\nMessaggio:\n"
                + request.getContent().trim());

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            // Non includere contenuto o indirizzo dell'utente nei log/eccezioni.
            throw new ContactMailException("Servizio email temporaneamente non disponibile", ex);
        }
    }

    private String sanitizeHeader(String value) {
        return value.replace("\r", " ").replace("\n", " ").trim();
    }
}
