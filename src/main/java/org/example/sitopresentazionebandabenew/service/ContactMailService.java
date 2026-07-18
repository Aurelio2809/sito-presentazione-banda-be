package org.example.sitopresentazionebandabenew.service;

import org.example.sitopresentazionebandabenew.dto.requests.MessageRequest;

public interface ContactMailService {
    void send(MessageRequest request);
}
